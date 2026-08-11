package com.likelion.staycare.domain.notification.scheduler;

import com.likelion.staycare.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReturnHomeReminderScheduler {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final NotificationService notificationService;

    // 매분 실행
    @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
    public void sendReminder() {
        LocalTime now = LocalTime.now(KOREA_ZONE)
                .withSecond(0)
                .withNano(0);

        LocalDate today = LocalDate.now(KOREA_ZONE);

        log.info("[Scheduler] 실행됨 - 현재시간={}, 오늘={}", now, today);

        notificationService.sendReturnHomeReminders(now, today);
    }
}
