package com.likelion.staycare.domain.mission.entity;

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

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "user_mission_step_checks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_mission_step_check_step",
                columnNames = {"step_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMissionStepCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "check_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    private GeneratedMissionStep generatedMissionStep;

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
    public UserMissionStepCheck(GeneratedMissionStep generatedMissionStep) {
        this.generatedMissionStep = generatedMissionStep;
        this.checked = false;
    }

    public void updateChecked(boolean checked) {
        this.checked = checked;
        this.checkedAt = checked ? LocalDateTime.now() : null;
    }
}
