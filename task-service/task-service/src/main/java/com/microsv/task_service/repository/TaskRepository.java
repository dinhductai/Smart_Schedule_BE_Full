package com.microsv.task_service.repository;

import com.microsv.task_service.entity.Task;
import com.microsv.task_service.enumeration.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByDeadlineBetweenAndStatus(
            LocalDateTime startDeadline,
            LocalDateTime endDeadline,
            TaskStatus status
    );
    Optional<Task>  findByTaskIdAndUserId(Long taskId, long userId);

    List<Task> findAllByUserId(Long userId);

    List<Task> findAllByUserIdAndStatus(Long userId, TaskStatus status);

    Long countByUserIdAndStatus(Long userId, TaskStatus status);

}
