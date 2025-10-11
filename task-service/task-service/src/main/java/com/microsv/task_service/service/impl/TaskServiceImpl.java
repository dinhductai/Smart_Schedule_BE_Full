package com.microsv.task_service.service.impl;

import com.microsv.task_service.dto.request.TaskCreationRequest;
import com.microsv.task_service.dto.request.TaskUpdateRequest;
import com.microsv.task_service.dto.response.TaskResponse;
import com.microsv.task_service.dto.response.TaskStatisticResponse;
import com.microsv.task_service.entity.Task;
import com.microsv.task_service.enumeration.PriorityLevel;
import com.microsv.task_service.enumeration.TaskStatus;
import com.microsv.task_service.repository.TaskRepository;
import com.microsv.task_service.service.TaskService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskCreationRequest request, Long userId) {
        validateDeadline(request.getDeadline());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(request.getPriority() != null ? request.getPriority() : PriorityLevel.MEDIUM)
                .status(TaskStatus.TODO)
                .userId(userId)
                .build();

        Task savedTask = taskRepository.save(task);
        return toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse getTask(Long taskId, Long userId) {
        Task task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found or access denied"
                ));
        return toTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasksByUser(Long userId) {
        List<Task> tasks = taskRepository.findAllByUserId(userId);
        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByStatus(Long userId, TaskStatus status) {
        List<Task> tasks = taskRepository.findAllByUserIdAndStatus(userId, status);
        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }



    @Override
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, Long userId) {
        Task task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found or access denied"
                ));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        validateDeadline(request.getDeadline());

        task.setDeadline(request.getDeadline());
        task.setPriority(request.getPriority());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            if (request.getStatus() == TaskStatus.DONE) {
                task.setCompletedAt(LocalDateTime.now());
            }
        }

        Task updatedTask = taskRepository.save(task);

        return toTaskResponse(updatedTask);
    }

    @Override
    public TaskResponse updateTaskStatus(Long taskId, TaskStatus status, Long userId) {
        Task task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found or access denied"
                ));

        task.setStatus(status);
        if (status == TaskStatus.DONE) {
            task.setCompletedAt(LocalDateTime.now());
        }

        Task updatedTask = taskRepository.save(task);

        return toTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task not found or access denied"
                ));

        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> getUpcomingTasks(Long userId, Integer hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadlineLimit = now.plusHours(hours != null ? hours : 24);

        List<Task> tasks = taskRepository.findAllByUserIdAndStatus(userId, TaskStatus.TODO)
                .stream()
                .filter(task -> task.getDeadline() != null &&
                        task.getDeadline().isAfter(now) &&
                        task.getDeadline().isBefore(deadlineLimit))
                .collect(Collectors.toList());

        return tasks.stream()
                .map(this::toTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskStatisticResponse getTaskStatistics(Long userId) {
        Long totalTasks = taskRepository.countByUserIdAndStatus(userId, null);
        Long todoCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.TODO);
        Long inProgressCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        Long doneCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.DONE);

        return TaskStatisticResponse.builder()
                .totalTasks(totalTasks)
                .todoCount(todoCount)
                .inProgressCount(inProgressCount)
                .doneCount(doneCount)
                .completionRate(totalTasks > 0 ? (double) doneCount / totalTasks * 100 : 0)
                .build();
    }

    //bắt lỗi deadline đặt ở quá khứ
    private void validateDeadline(LocalDateTime deadline) {
        if (deadline != null && deadline.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Deadline must be in the future"
            );
        }
    }

    private TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .completedAt(task.getCompletedAt())
                .userId(task.getUserId())
                .build();
    }
}