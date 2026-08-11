package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.*;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
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
        String careMotivationLabel
) {
    public static DiagnosisResponse from(Diagnosis d) {
        return DiagnosisResponse.builder()
                .diagnosisId(d.getId())
                .gender(d.getGender().getLabel())
                .age(d.getAge())
                .skinTypeLabel(d.getSkinType().getLabel())
                .wakeUpTime(d.getWakeUpTime())
                .returnHomeTime(d.getReturnHomeTime())
                .checkCycleDays(d.getCheckCycle().getDays())
                .careMotivationLabel(d.getCareMotivation().getLabel())
                .build();
    }
}
