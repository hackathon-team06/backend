package com.likelion.staycare.domain.mission.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MorningMissionResponse(
        String title,
        String description,
        List<Long> stepIds,
        List<String> steps,
        String tip
) {
}
