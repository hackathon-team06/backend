package com.likelion.staycare.domain.mission.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "user_morning_step_checks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_morning_step_check",
                columnNames = {"user_id", "step_id", "mission_date"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMorningStepCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private MorningMissionStep morningMissionStep;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;

    @Column(nullable = false)
    private boolean checked;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public UserMorningStepCheck(User user, MorningMissionStep morningMissionStep, LocalDate missionDate) {
        this.user = user;
        this.morningMissionStep = morningMissionStep;
        this.missionDate = missionDate;
        this.checked = false;
    }

    public void updateChecked(boolean checked) {
        this.checked = checked;
        this.checkedAt = checked ? LocalDateTime.now() : null;
    }
}
