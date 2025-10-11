package com.microsv.task_service.service;

import com.microsv.task_service.dto.request.TaskCreationRequest;
import com.microsv.task_service.dto.request.TaskUpdateRequest;
import com.microsv.task_service.dto.response.TaskResponse;
import com.microsv.task_service.dto.response.TaskStatisticResponse;
import com.microsv.task_service.enumeration.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskCreationRequest request, Long userId);
    TaskResponse getTask(Long taskId, Long userId);
    List<TaskResponse> getAllTasksByUser(Long userId);
    List<TaskResponse> getTasksByStatus(Long userId, TaskStatus status);
    void deleteTask(Long taskId, Long userId);
    TaskResponse updateTask(Long taskId, TaskUpdateRequest request, Long userId);
    TaskStatisticResponse getTaskStatistics(Long userId);
    TaskResponse updateTaskStatus(Long taskId, TaskStatus status, Long userId);
    List<TaskResponse> getUpcomingTasks(Long userId, Integer hours);
}