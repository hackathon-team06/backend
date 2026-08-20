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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final UserRepository userRepository;
    private final PushTokenRepository pushTokenRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FcmSender fcmSender;

    /**
     * 사용자 푸시 토큰 저장/갱신
     * - 기존 토큰이 있으면 token 값 갱신 + active=true 재활성화
     * - 없으면 신규 저장
     */
    public void savePushToken(Long userId, PushTokenRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        log.info("[NotificationService] 푸시토큰 저장 요청 - userId={}", userId);

        pushTokenRepository.findByUserId(userId)
                .ifPresentOrElse(
                        pushToken -> {
                            pushToken.updateToken(request.token());
                            log.info(
                                    "[NotificationService] 푸시토큰 갱신 완료 - userId={}, tokenId={}, active={}",
                                    userId,
                                    pushToken.getId(),
                                    pushToken.getActive()
                            );
                        },
                        () -> {
                            PushToken saved = pushTokenRepository.save(
                                    PushToken.builder()
                                            .user(user)
                                            .token(request.token())
                                            .build()
                            );
                            log.info(
                                    "[NotificationService] 푸시토큰 신규 저장 완료 - userId={}, tokenId={}, active={}",
                                    userId,
                                    saved.getId(),
                                    saved.getActive()
                            );
                        }
                );
    }

    /**
     * 테스트용 푸시 알림 발송
     */
    public void sendTestNotification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        PushToken pushToken = pushTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 푸시 토큰이 없습니다. 먼저 토큰을 저장해주세요."));

        if (!Boolean.TRUE.equals(pushToken.getActive())) {
            throw new IllegalStateException("비활성화된 푸시 토큰입니다.");
        }

        if (pushToken.getToken() == null || pushToken.getToken().isBlank()) {
            throw new IllegalStateException("푸시 토큰 값이 비어 있습니다.");
        }

        log.info(
                "[NotificationService] 테스트 알림 발송 시작 - userId={}, tokenId={}, active={}",
                user.getId(),
                pushToken.getId(),
                pushToken.getActive()
        );

        fcmSender.send(
                pushToken.getToken(),
                "StayCare 테스트 알림",
                "FCM 연동이 정상적으로 완료되었습니다."
        );

        log.info("[NotificationService] 테스트 알림 발송 완료 - userId={}", user.getId());
    }

    /**
     * 귀가 리마인더 발송
     * - notification_enabled=true
     * - return_home_time == targetTime
     * 인 사용자 대상으로 발송
     */
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

            Optional<PushToken> pushTokenOpt = pushTokenRepository.findByUserId(user.getId());

            log.info(
                    "[NotificationService] pushToken 조회 결과 - userId={}, present={}",
                    user.getId(),
                    pushTokenOpt.isPresent()
            );

            if (pushTokenOpt.isEmpty()) {
                log.info("[NotificationService] 푸시토큰 레코드 없음 - userId={}", user.getId());
                continue;
            }

            PushToken pushToken = pushTokenOpt.get();

            log.info(
                    "[NotificationService] 푸시토큰 상태 - userId={}, tokenId={}, active={}, tokenBlank={}",
                    user.getId(),
                    pushToken.getId(),
                    pushToken.getActive(),
                    pushToken.getToken() == null || pushToken.getToken().isBlank()
            );

            if (!Boolean.TRUE.equals(pushToken.getActive())) {
                log.info("[NotificationService] 비활성 푸시토큰 - userId={}, tokenId={}", user.getId(), pushToken.getId());
                continue;
            }

            if (pushToken.getToken() == null || pushToken.getToken().isBlank()) {
                log.info("[NotificationService] 빈 푸시토큰 - userId={}, tokenId={}", user.getId(), pushToken.getId());
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
                log.warn(
                        "[NotificationService] 알림 발송 실패 - userId={}, tokenId={}, message={}",
                        user.getId(),
                        pushToken.getId(),
                        e.getMessage(),
                        e
                );
            }

            notificationLogRepository.save(
                    NotificationLog.builder()
                            .user(user)
                            .notificationType(NotificationType.RETURN_HOME_REMINDER)
                            .targetDate(today)
                            .success(success)
                            .build()
            );

            log.info(
                    "[NotificationService] 알림 로그 저장 완료 - userId={}, success={}",
                    user.getId(),
                    success
            );
        }

        log.info("[NotificationService] 알림 발송 종료");
    }
}
