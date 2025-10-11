package com.microsv.ai_service.dto.response;


import com.microsv.ai_service.enumeration.PriorityLevel;
import com.microsv.ai_service.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status;
    private PriorityLevel priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private Long userId;
}