package com.microsv.ai_service.service.impl;

import com.microsv.ai_service.service.ChatAIService;
import com.microsv.ai_service.util.PromptUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatAIServiceImpl implements ChatAIService {
    ChatClient chatClient;

    public ChatAIServiceImpl(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultSystem(PromptUtil.SYSTEM_PROMPT) //prompt để train AI
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    //tạm thời trả ra string, đang bị lỗi convert response object
    @Override
    public String chat(String message, MultipartFile file, String conversationId, Long userId) {

        //chưa xử lý multipart file

        if (message == null || message.trim().isEmpty()) {
            message = "Xin chào"; //message mặc định
        }

        String finalMessage = message;
        return chatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(promptUserSpec -> {
                    if (finalMessage != null && !finalMessage.isEmpty()) {
                        promptUserSpec.text(finalMessage);
                    }
                })

                //đang thiếu dạng file

                .call()
                .content().trim();

    }
}
