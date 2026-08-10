package com.likelion.staycare.domain.mission.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record EveningMissionResponse(
        String title,
        String description,
        List<String> steps,
        String tip
) {
}
