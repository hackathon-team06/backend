package com.likelion.staycare.domain.notification.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Table(
        name = "notification_logs",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "notification_type", "target_date"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private Boolean success;

    @Builder
    public NotificationLog(User user, NotificationType notificationType, LocalDate targetDate, Boolean success) {
        this.user = user;
        this.notificationType = notificationType;
        this.targetDate = targetDate;
        this.success = success;
    }
}
