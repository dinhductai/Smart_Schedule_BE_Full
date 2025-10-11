package com.microsv.ai_service.service.impl;

import com.microsv.ai_service.client.TaskClient;
import com.microsv.ai_service.dto.response.TaskResponse;
import com.microsv.ai_service.entity.ConversationMemory;
import com.microsv.ai_service.repository.ConversationMemoryRepository;
import com.microsv.ai_service.util.NullUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMemoryServiceImpl implements ChatMemory {
    private final ConversationMemoryRepository conversationMemoryRepository;
    private final TaskClient taskClient;

    @Override
    public void add(String conversationId, List<Message> messages) {
        Long currentUserId = getCurrentUserId();
        NullUtil.checkUserNullByUserId(currentUserId);

        for (Message message : messages) {
            ConversationMemory cvMemory = ConversationMemory.builder()
                    .conversationId(conversationId)
                    .role(message.getMessageType().getValue().toUpperCase())
                    .content(message.getText())
                    .userId(currentUserId)
                    .build();
            conversationMemoryRepository.save(cvMemory);
        }
    }

    @Override
    public List<Message> get(String conversationId) {
        NullUtil.checkUserNullByUserId(getCurrentUserId());
        List<ConversationMemory> cvMemory = conversationMemoryRepository.findByConversationId(conversationId);
        List<TaskResponse> tasks = taskClient.getUserTasks(getCurrentUserId());
        Message taskContext = createTaskContext(tasks);
        List<Message> messages = new ArrayList<>();
        if(!tasks.isEmpty()){
            messages.add(taskContext);
        }
        messages.addAll(cvMemory.stream().map(this::convertToMessage).collect(Collectors.toList()));
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        Long currentUserId = getCurrentUserId();
        NullUtil.checkUserNullByUserId(currentUserId);
        conversationMemoryRepository.deleteByConversationIdAndUserId(conversationId, currentUserId);
    }

    private Message createTaskContext(List<TaskResponse> taskResponses) {
        StringBuilder context = new StringBuilder();
        context.append("USER'S CURRENT TASKS , DEADLINE AND SCHEDULE: \n");
        if (taskResponses.isEmpty()) {
            context.append(" NO TASKS FOUND\n");
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for(TaskResponse taskResponse : taskResponses){
                context.append(String.format(
                        "- title: %s | description: %s | deadline: %s | status: %s | priority: %s | createdAt: %s | completedAt: %s \n",
                        taskResponse.getTitle(),
                        taskResponse.getDescription(),
                        taskResponse.getDeadline() != null ? taskResponse.getDeadline().format(formatter) : "No deadline",
                        taskResponse.getStatus(),
                        taskResponse.getPriority(),
                        taskResponse.getCreatedAt().format(formatter),
                        taskResponse.getCompletedAt() != null ? taskResponse.getCompletedAt().format(formatter) : "Still working"
                ));
            }
        }
        context.append("\nUse this task information to provide relevant responses about scheduling, " +
                "task management, and deadlines while maintaining conversation context.");
        return new SystemMessage(context.toString());
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return Long.parseLong(jwt.getSubject());
        }
        return null;
    }

    private Message convertToMessage(ConversationMemory memory){
        if("USER".equalsIgnoreCase(memory.getRole())){
            return new UserMessage(memory.getContent());
        }
        else {
            return new AssistantMessage(memory.getContent());
        }
    }
}