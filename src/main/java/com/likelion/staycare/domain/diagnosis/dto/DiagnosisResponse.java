package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.Diagnosis;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record DiagnosisResponse(
        Long diagnosisId,
        String gender,
        Integer age,
        String skinTypeLabel,
        LocalTime wakeUpTime,
        LocalTime returnHomeTime,
        Integer checkCycleDays,
        String careMotivationLabel,
        String recommendation,
        int awardedPoint,
        int totalPoint
) {
    public static DiagnosisResponse from(Diagnosis d) {
        return DiagnosisResponse.builder()
                .diagnosisId(d.getId())
                .gender(d.getGender() != null ? d.getGender().getLabel() : null)
                .age(d.getAge())
                .skinTypeLabel(d.getSkinType() != null ? d.getSkinType().getLabel() : null)
                .wakeUpTime(d.getWakeUpTime())
                .returnHomeTime(d.getReturnHomeTime())
                .checkCycleDays(d.getCheckCycle() != null ? d.getCheckCycle().getDays() : null)
                .careMotivationLabel(d.getCareMotivation() != null ? d.getCareMotivation().getLabel() : null)
                .recommendation(d.getRecommendation())
                .awardedPoint(0)
                .totalPoint(0)
                .build();
    }

    public static DiagnosisResponse from(Diagnosis d, int awardedPoint, int totalPoint) {
        return DiagnosisResponse.builder()
                .diagnosisId(d.getId())
                .gender(d.getGender() != null ? d.getGender().getLabel() : null)
                .age(d.getAge())
                .skinTypeLabel(d.getSkinType() != null ? d.getSkinType().getLabel() : null)
                .wakeUpTime(d.getWakeUpTime())
                .returnHomeTime(d.getReturnHomeTime())
                .checkCycleDays(d.getCheckCycle() != null ? d.getCheckCycle().getDays() : null)
                .careMotivationLabel(d.getCareMotivation() != null ? d.getCareMotivation().getLabel() : null)
                .recommendation(d.getRecommendation())
                .awardedPoint(awardedPoint)
                .totalPoint(totalPoint)
                .build();
    }
}
