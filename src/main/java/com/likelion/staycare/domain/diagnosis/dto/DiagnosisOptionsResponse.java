package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.AgeRange;
import com.likelion.staycare.domain.diagnosis.entity.CareMotivation;
import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.ReturnHomeTime;
import com.likelion.staycare.domain.diagnosis.entity.SleepHours;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record DiagnosisOptionsResponse(
        List<String> ageRanges,
        List<String> sleepHours,
        List<String> skinTypes,
        List<String> outingFrequencies,
        List<String> checkCycles,
        List<String> careMotivations
) {
    public static DiagnosisOptionsResponse getAll() {
        return DiagnosisOptionsResponse.builder()
                .ageRanges(Arrays.stream(AgeRange.values())
                        .map(AgeRange::getLabel)
                        .toList())
                .sleepHours(Arrays.stream(SleepHours.values())
                        .map(SleepHours::getLabel)
                        .toList())
                .skinTypes(Arrays.stream(SkinType.values())
                        .map(SkinType::getLabel)
                        .toList())
                .outingFrequencies(Arrays.stream(ReturnHomeTime.values())
                        .map(ReturnHomeTime::getLabel)
                        .toList())
                .checkCycles(Arrays.stream(CheckCycle.values())
                        .map(CheckCycle::getLabel)
                        .toList())
                .careMotivations(Arrays.stream(CareMotivation.values())
                        .map(CareMotivation::getLabel)
                        .toList())
                .build();
    }
}
