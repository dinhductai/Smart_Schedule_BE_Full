package com.microsv.task_service.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatisticResponse {
    Long totalTasks;
    Long todoCount;
    Long inProgressCount;
    Long doneCount;
    Double completionRate;
}
