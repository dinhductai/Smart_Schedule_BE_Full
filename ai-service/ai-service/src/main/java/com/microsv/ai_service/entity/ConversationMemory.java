package com.microsv.ai_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "conversation_memory")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationMemory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Integer chatId;

    @Column(name = "conversation_id",nullable = false)
    private String conversationId;

    @Column(name = "role",nullable = false)
    private String role;

    @Column(name = "content",length = 3000) //tăng kích thước lưu trữ câu tl của AI
    private String content;

    @Column(name = "create_at")
    private Timestamp createAt = new Timestamp(System.currentTimeMillis());

    @Column(name = "user_id",nullable = false)
    private Long userId;
}
