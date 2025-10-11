package com.microsv.task_service.service.impl;

import com.microsv.task_service.dto.request.SubscriptionRequest;
import com.microsv.task_service.entity.PushSubscription;
import com.microsv.task_service.entity.Task;
import com.microsv.task_service.enumeration.TaskStatus;
import com.microsv.task_service.repository.PushSubscriptionRepository;
import com.microsv.task_service.repository.TaskRepository;
import com.microsv.task_service.service.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationServiceImpl implements NotificationService {

    TaskRepository taskRepository;
    PushSubscriptionRepository subscriptionRepository;

    // PushService không final vì khởi tạo sau trong @PostConstruct
    PushService pushService;

    @Value("${vapid.public.key}")
    String publicKey;

    @Value("${vapid.private.key}")
    String privateKey;


    //khởi tạo pushservice với cặp khóa
    @PostConstruct
    private void init() throws GeneralSecurityException {
        //đảm bảo BouncyCastle được đăng ký
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        this.pushService = new PushService(publicKey, privateKey);
    }


    //quét deadline và gửi tb
    @Override
    @Scheduled(fixedRate = 30000) //set thời gian quét deadline , đang là 30s
    public void checkDeadlinesAndSendNotifications() {
        log.info("scanning for upcoming deadlines");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inOneHour = now.plusHours(1);

        List<Task> upcomingTasks = taskRepository.findAllByDeadlineBetweenAndStatus(now, inOneHour, TaskStatus.TODO);

        for (Task task : upcomingTasks) {
            List<PushSubscription> subscriptions = subscriptionRepository.findAllByUserId(task.getUserId());

            for (PushSubscription sub : subscriptions) {
                String payload = String.format("task '%s' is due soon " , task.getTitle());
                sendNotification(sub, payload);
            }
        }
    }


    //lưu đăng ký nhận tb
    @Override
    public void subscribe(SubscriptionRequest request, Long userId) {
        PushSubscription subscription = new PushSubscription();
        subscription.setUserId(userId);
        subscription.setEndpoint(request.getEndpoint());
        subscription.setP256dh(request.getP256dh());
        subscription.setAuth(request.getAuth());
        subscriptionRepository.save(subscription);
    }

    //gửi thông báo trình duyệt
    public void sendNotification(PushSubscription subscription, String payload) {
        try {
            Notification notification = new Notification(
                    subscription.getEndpoint(),
                    subscription.getP256dh(),
                    subscription.getAuth(),
                    payload
            );

            pushService.send(notification);

        } catch (Exception e) {
            log.error("Error sending push notification: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
