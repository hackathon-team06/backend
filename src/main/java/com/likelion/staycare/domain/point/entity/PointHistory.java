package com.likelion.staycare.domain.point.entity;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "point_histories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_point_history_user_step_reward",
                        columnNames = {"user_id", "step_id", "reward_type"}
                ),
                @UniqueConstraint(
                        name = "uk_point_history_user_mission_reward",
                        columnNames = {"user_id", "mission_id", "reward_type"}
                ),
                @UniqueConstraint(
                        name = "uk_point_history_user_reward_key",
                        columnNames = {"user_id", "reward_type", "reward_key"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private GeneratedMission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private GeneratedMissionStep step;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false, length = 40)
    private PointRewardType rewardType;

    @Column(name = "reward_key", length = 100)
    private String rewardKey;

    @Column(nullable = false)
    private Integer amount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PointHistory(
            User user,
            GeneratedMission mission,
            GeneratedMissionStep step,
            PointRewardType rewardType,
            String rewardKey,
            Integer amount
    ) {
        this.user = user;
        this.mission = mission;
        this.step = step;
        this.rewardType = rewardType;
        this.rewardKey = rewardKey;
        this.amount = amount;
    }
}
