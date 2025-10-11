package com.microsv.task_service.dto.request;

import lombok.*;

@Builder
@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    private String endpoint;
    private String p256dh;
    private String auth;
}