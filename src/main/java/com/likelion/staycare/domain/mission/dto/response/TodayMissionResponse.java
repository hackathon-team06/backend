package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "오늘 생성된 아침/저녁 미션 묶음 응답")
public record TodayMissionResponse(
        @Schema(description = "오늘 아침 미션. 아직 생성되지 않았다면 null 가능", nullable = true)
        MorningMissionResponse morningMission,

        @Schema(description = "오늘 저녁 미션. 아직 생성되지 않았다면 null 가능", nullable = true)
        EveningMissionResponse eveningMission
) {
}
