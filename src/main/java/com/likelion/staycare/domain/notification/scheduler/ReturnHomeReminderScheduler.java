package com.likelion.staycare.domain.notification.scheduler;

import com.likelion.staycare.domain.diagnosis.entity.ReturnHomeTime;
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

    private final NotificationService notificationService;

    // 운영용: 매시간 정각 실행
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    public void sendReminder() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"))
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        log.info("[Scheduler] 실행됨 - 현재시간={}", now);

        ReturnHomeTime slot = ReturnHomeTime.from(now);

        log.info("[Scheduler] 매핑된 슬롯={}", slot);

        if (slot == null) {
            log.info("[Scheduler] 현재 시간에 매칭되는 귀가 시간 슬롯 없음");
            return;
        }

        notificationService.sendReturnHomeReminders(
                slot,
                LocalDate.now(ZoneId.of("Asia/Seoul"))
        );
    }
}
