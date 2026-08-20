package com.likelion.staycare.domain.stamp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "stamp_reward_policies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stamp_reward_policy_period_completed",
                        columnNames = {"period_days", "completed_days"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StampRewardPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_reward_policy_id")
    private Long id;

    @Column(name = "period_days", nullable = false)
    private Integer periodDays; // 7, 14, 21, 28

    @Column(name = "completed_days", nullable = false)
    private Integer completedDays; // 0 ~ periodDays

    @Column(name = "reward_point", nullable = false)
    private Integer rewardPoint;

    @Builder
    public StampRewardPolicy(Integer periodDays, Integer completedDays, Integer rewardPoint) {
        this.periodDays = periodDays;
        this.completedDays = completedDays;
        this.rewardPoint = rewardPoint;
    }
}
