package com.likelion.staycare.domain.notification.repository;

import com.likelion.staycare.domain.notification.entity.NotificationLog;
import com.likelion.staycare.domain.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    boolean existsByUserIdAndNotificationTypeAndTargetDateAndSuccessTrue(
            Long userId,
            NotificationType notificationType,
            LocalDate targetDate
    );
}
