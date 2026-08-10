package com.likelion.staycare.domain.mission.dto.request;

public record EveningMissionRequest(
        String age,
        String skinType,
        String goal,
        String checkCycle,
        String todaySchedule,
        String todaySkinCondition
) {
}
