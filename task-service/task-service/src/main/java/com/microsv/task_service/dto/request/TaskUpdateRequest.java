package com.microsv.task_service.dto.request;

import com.microsv.task_service.enumeration.PriorityLevel;
import com.microsv.task_service.enumeration.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class TaskUpdateRequest {
    private String title;
    private String description;
    private LocalDateTime deadline;
    private PriorityLevel priority;
    private TaskStatus status;
}
