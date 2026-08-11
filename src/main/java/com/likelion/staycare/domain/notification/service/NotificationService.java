package com.likelion.staycare.domain.notification.service;

import com.likelion.staycare.domain.notification.dto.PushTokenRequest;
import com.likelion.staycare.domain.notification.entity.NotificationLog;
import com.likelion.staycare.domain.notification.entity.NotificationType;
import com.likelion.staycare.domain.notification.entity.PushToken;
import com.likelion.staycare.domain.notification.repository.NotificationLogRepository;
import com.likelion.staycare.domain.notification.repository.PushTokenRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final UserRepository userRepository;
    private final PushTokenRepository pushTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FcmSender fcmSender;

    public void savePushToken(Long userId, PushTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        pushTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        pushToken -> pushToken.updateToken(request.token()),
                        () -> pushTokenRepository.save(
                                PushToken.builder()
                                        .user(user)
                                        .token(request.token())
                                        .build()
                        )
                );
    }

    public void sendTestNotification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        PushToken pushToken = pushTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 푸시 토큰이 없습니다. 먼저 토큰을 저장해주세요."));

        if (!Boolean.TRUE.equals(pushToken.getActive())) {
            throw new IllegalStateException("비활성화된 푸시 토큰입니다.");
        }

        log.info("[NotificationService] 테스트 알림 발송 시작 - userId={}", user.getId());

        fcmSender.send(
                pushToken.getToken(),
                "StayCare 테스트 알림",
                "FCM 연동이 정상적으로 완료되었습니다."
        );

        log.info("[NotificationService] 테스트 알림 발송 완료 - userId={}", user.getId());
    }

    public void sendReturnHomeReminders(LocalTime targetTime, LocalDate today) {
        log.info("[NotificationService] 알림 발송 시작 - slot={}, today={}", targetTime, today);

        List<User> users = userRepository.findAllByNotificationEnabledTrueAndReturnHomeTime(targetTime);
        log.info("[NotificationService] 조회된 사용자 수={}", users.size());

        for (User user : users) {
            boolean alreadySent = notificationLogRepository
                    .existsByUserIdAndNotificationTypeAndTargetDateAndSuccessTrue(
                            user.getId(),
                            NotificationType.RETURN_HOME_REMINDER,
                            today
                    );

            if (alreadySent) {
                log.info("[NotificationService] 이미 발송 완료 - userId={}", user.getId());
                continue;
            }

            PushToken pushToken = pushTokenRepository.findByUserId(user.getId()).orElse(null);

            if (pushToken == null) {
                log.info("[NotificationService] 푸시토큰 없음 - userId={}", user.getId());
                continue;
            }

            if (!Boolean.TRUE.equals(pushToken.getActive())) {
                log.info("[NotificationService] 비활성 푸시토큰 - userId={}", user.getId());
                continue;
            }

            boolean success = false;

            try {
                fcmSender.send(
                        pushToken.getToken(),
                        "오늘의 귀가 미션",
                        "집에 도착했다면 위치 확인 후 미션을 시작해보세요."
                );
                success = true;
                user.markNotified(today);

                log.info("[NotificationService] 알림 발송 성공 - userId={}", user.getId());

            } catch (Exception e) {
                log.warn("[NotificationService] 알림 발송 실패 - userId={}, message={}", user.getId(), e.getMessage());
            }

            notificationLogRepository.save(
                    NotificationLog.builder()
                            .user(user)
                            .notificationType(NotificationType.RETURN_HOME_REMINDER)
                            .targetDate(today)
                            .success(success)
                            .build()
            );
        }

        log.info("[NotificationService] 알림 발송 종료");
    }
}
