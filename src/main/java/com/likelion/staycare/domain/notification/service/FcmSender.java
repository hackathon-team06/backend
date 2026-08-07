package com.likelion.staycare.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.WebpushConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class FcmSender {

    public void send(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setWebpushConfig(WebpushConfig.builder()
                            .putHeader("ttl", "300")
                            .putAllData(Map.of(
                                    "title", title,
                                    "body", body,
                                    "type", "RETURN_HOME_REMINDER"
                            ))
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("[FcmSender] FCM 발송 성공 - response={}", response);

        } catch (Exception e) {
            log.error("[FcmSender] FCM 발송 실패 - token={}, error={}",
                    token != null && token.length() > 20 ? token.substring(0, 20) + "..." : token,
                    e.getMessage(), e);
            throw new RuntimeException("FCM 발송 실패", e);
        }
    }
}
