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

@Entity
@Getter
@Table(
        name = "generated_mission_steps",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_generated_mission_step_order",
                columnNames = {"generated_mission_id", "step_order"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GeneratedMissionStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "step_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_mission_id", nullable = false)
    private GeneratedMission generatedMission;

    @Column(nullable = false, length = 300)
    private String content;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Builder
    public GeneratedMissionStep(GeneratedMission generatedMission, String content, int stepOrder) {
        this.generatedMission = generatedMission;
        this.content = content;
        this.stepOrder = stepOrder;
    }
}
