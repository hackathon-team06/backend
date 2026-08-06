package com.likelion.staycare.domain.mission.entity;

import com.likelion.staycare.domain.mission.entity.enums.MissionStatus;
import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "generated_missions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_generated_mission_user_date",
                        columnNames = {"user_id", "mission_date"}
                ),
                @UniqueConstraint(
                        name = "uk_generated_mission_skin_check",
                        columnNames = {"skin_check_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneratedMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "generated_mission_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_check_id", nullable = false, unique = true)
    private DailySkinCheck dailySkinCheck;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tip;

    @Column(name = "prompt_version", length = 30)
    private String promptVersion;

    @Column(name = "mission_date", nullable = false)
    private LocalDate missionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MissionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public GeneratedMission(
            User user,
            DailySkinCheck dailySkinCheck,
            String title,
            String description,
            String tip,
            String promptVersion,
            LocalDate missionDate
    ) {
        this.user = user;
        this.dailySkinCheck = dailySkinCheck;
        this.title = title;
        this.description = description;
        this.tip = tip;
        this.promptVersion = promptVersion;
        this.missionDate = missionDate;
        this.status = MissionStatus.PENDING;
    }

    public void complete() {
        this.status = MissionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
