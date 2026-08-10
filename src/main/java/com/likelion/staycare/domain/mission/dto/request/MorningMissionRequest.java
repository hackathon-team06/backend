package com.likelion.staycare.domain.mission.dto.request;

public record MorningMissionRequest(
        String age,
        String skinType,
        String goal,
        String checkCycle,
        String todaySchedule,
        String previousEveningMission,
        String previousEveningMissionResult,
        String previousSkinCondition
) {
}
