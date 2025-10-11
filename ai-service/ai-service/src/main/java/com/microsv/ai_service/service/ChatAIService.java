package com.microsv.ai_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface ChatAIService {
    String chat(String message, MultipartFile file, String conversationId, Long userId);

}
