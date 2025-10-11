package com.microsv.ai_service.controller;

import com.microsv.ai_service.dto.response.ChatAIConversationResponse;
import com.microsv.ai_service.dto.response.ChatAIResponse;
import com.microsv.ai_service.service.impl.ChatAIServiceImpl;
import com.microsv.ai_service.service.impl.ChatMemoryServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/ai")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConversationController {
    ChatMemoryServiceImpl chatMemoryService;
    ChatAIServiceImpl chatAIService;

    @PostMapping
    public ResponseEntity<ChatAIConversationResponse> chat(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "conversationId", required = false) String conversationId,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId  = Long.parseLong(jwt.getSubject());
        if (conversationId == null || conversationId.isBlank()) {
            conversationId = UUID.randomUUID().toString();
        }
        String chatAIResponses = chatAIService.chat(message, file,conversationId,userId);
        ChatAIConversationResponse response = new ChatAIConversationResponse(conversationId,chatAIResponses);
        return ResponseEntity.ok(response);
    }

}
