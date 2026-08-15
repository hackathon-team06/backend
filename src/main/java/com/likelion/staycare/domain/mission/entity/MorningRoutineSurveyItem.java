package com.likelion.staycare.domain.mission.entity;

import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineSurveyOption;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "morning_routine_survey_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_morning_routine_survey_item_order",
                columnNames = {"morning_routine_survey_id", "item_order"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MorningRoutineSurveyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "morning_routine_survey_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "morning_routine_survey_id", nullable = false)
    private MorningRoutineSurvey morningRoutineSurvey;

    @Enumerated(EnumType.STRING)
    @Column(name = "option_code", nullable = false, length = 50)
    private MorningRoutineSurveyOption optionCode;

    @Column(name = "item_order", nullable = false)
    private int itemOrder;

    @Builder
    public MorningRoutineSurveyItem(
            MorningRoutineSurvey morningRoutineSurvey,
            MorningRoutineSurveyOption optionCode,
            int itemOrder
    ) {
        this.morningRoutineSurvey = morningRoutineSurvey;
        this.optionCode = optionCode;
        this.itemOrder = itemOrder;
    }
}
