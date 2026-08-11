package com.likelion.staycare.domain.diagnosis.dto;

import com.likelion.staycare.domain.diagnosis.entity.CareMotivation;
import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import lombok.Builder;

import java.util.Arrays;
import java.util.List;

@Builder
public record DiagnosisOptionsResponse(
        List<String> skinTypes,
        List<String> checkCycles,
        List<String> careMotivations
) {
    public static DiagnosisOptionsResponse getAll() {
        return DiagnosisOptionsResponse.builder()
                .skinTypes(Arrays.stream(SkinType.values())
                        .map(SkinType::getLabel)
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
