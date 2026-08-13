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
        int awardedPoint,
        int totalPoint
) {
    public static DiagnosisResponse from(Diagnosis d) {
        return from(d, 0, 0);
    }

    public static DiagnosisResponse from(Diagnosis d, int awardedPoint, int totalPoint) {
        return DiagnosisResponse.builder()
                .diagnosisId(d.getId())
                .gender(d.getGender().getLabel())
                .age(d.getAge())
                .skinTypeLabel(d.getSkinType().getLabel())
                .wakeUpTime(d.getWakeUpTime())
                .returnHomeTime(d.getReturnHomeTime())
                .checkCycleDays(d.getCheckCycle().getDays())
                .careMotivationLabel(d.getCareMotivation().getLabel())
                .awardedPoint(awardedPoint)
                .totalPoint(totalPoint)
                .build();
    }
}
