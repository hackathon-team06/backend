package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.*;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import lombok.Builder;

@Builder
public record DiagnosisResponse(
        Long diagnosisId,
        String ageRangeLabel,
        String sleepHoursLabel,
        String skinTypeLabel,
        String outingFrequencyLabel,
        Integer checkCycleDays,
        String careMotivationLabel
) {
    public static DiagnosisResponse from(Diagnosis d) {
        return DiagnosisResponse.builder()
                .diagnosisId(d.getId())
                .ageRangeLabel(d.getAgeRange().getLabel())
                .sleepHoursLabel(d.getSleepHours().getLabel())
                .skinTypeLabel(d.getSkinType().getLabel())
                .outingFrequencyLabel(d.getOutingFrequency().getLabel())
                .checkCycleDays(d.getCheckCycle().getDays())
                .careMotivationLabel(d.getCareMotivation().getLabel())
                .build();
    }
}
