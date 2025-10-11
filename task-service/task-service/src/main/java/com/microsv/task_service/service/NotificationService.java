package com.microsv.task_service.service;

import com.microsv.task_service.dto.request.SubscriptionRequest;

public interface NotificationService {
    void checkDeadlinesAndSendNotifications();

    void subscribe(SubscriptionRequest request, Long userId);
}
