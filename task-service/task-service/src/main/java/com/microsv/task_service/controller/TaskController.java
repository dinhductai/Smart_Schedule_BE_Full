package com.microsv.task_service.controller;

import com.microsv.task_service.dto.request.TaskCreationRequest;
import com.microsv.task_service.dto.request.TaskUpdateRequest;
import com.microsv.task_service.dto.response.TaskResponse;
import com.microsv.task_service.dto.response.TaskStatisticResponse;
import com.microsv.task_service.enumeration.TaskStatus;
import com.microsv.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    //tạo task mới
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreationRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        TaskResponse response = taskService.createTask(request, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //lấy tất cả tasks của user
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        List<TaskResponse> responses = taskService.getAllTasksByUser(userId);
        return ResponseEntity.ok(responses);
    }

    //  lấy task theo id
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId,
                                                @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        TaskResponse response = taskService.getTask(taskId, userId);
        return ResponseEntity.ok(response);
    }

    //lấy tasks theo status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable TaskStatus status,
                                                               @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        List<TaskResponse> responses = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(responses);
    }

    //lấy tasks sắp đến hạn
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskResponse>> getUpcomingTasks(
            @RequestParam(required = false, defaultValue = "24") Integer hours,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        List<TaskResponse> responses = taskService.getUpcomingTasks(userId, hours);
        return ResponseEntity.ok(responses);
    }

    //lấy thống kê tasks
    @GetMapping("/statistics")
    public ResponseEntity<TaskStatisticResponse> getTaskStatistics(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        TaskStatisticResponse statistics = taskService.getTaskStatistics(userId);
        return ResponseEntity.ok(statistics);
    }

    //Cập nhật toàn bộ task
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId,
                                                   @Valid @RequestBody TaskUpdateRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        TaskResponse response = taskService.updateTask(taskId, request, userId);
        return ResponseEntity.ok(response);
    }

    // cập nhật status của task
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long taskId,
                                                         @RequestParam TaskStatus status,
                                                         @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        TaskResponse response = taskService.updateTaskStatus(taskId, status, userId);
        return ResponseEntity.ok(response);
    }

    //Xóa task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId,
                                           @AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.parseLong(jwt.getSubject());
        taskService.deleteTask(taskId, userId);
        return ResponseEntity.noContent().build();
    }
}