package com.likelion.staycare.domain.mission.dto.request;

public record EveningMissionRequest(
        String age,
        String skinType,
        String todaySkinCondition
) {
}
