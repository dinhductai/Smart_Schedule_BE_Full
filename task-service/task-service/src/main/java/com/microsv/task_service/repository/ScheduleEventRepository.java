package com.microsv.task_service.repository;

import com.microsv.task_service.entity.ScheduleEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, Long> {
}