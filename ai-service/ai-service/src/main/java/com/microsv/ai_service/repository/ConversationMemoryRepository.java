package com.microsv.ai_service.repository;

import com.microsv.ai_service.dto.response.ChatAIResponse;
import com.microsv.ai_service.entity.ConversationMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Repository
public interface ConversationMemoryRepository extends JpaRepository<ConversationMemory, Long> {
    List<ConversationMemory> findByConversationId(String conversationId);
    void deleteByConversationIdAndUserId(String conversationId, Long userId);
}
