package com.microsv.task_service.repository;

import com.microsv.task_service.entity.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findAllByUserId(Long userId);
}