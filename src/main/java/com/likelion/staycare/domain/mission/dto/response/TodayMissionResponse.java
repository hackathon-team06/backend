package com.likelion.staycare.domain.mission.dto.response;

import lombok.Builder;

@Builder
public record TodayMissionResponse(
        MorningMissionResponse morningMission,
        EveningMissionResponse eveningMission
) {
}
