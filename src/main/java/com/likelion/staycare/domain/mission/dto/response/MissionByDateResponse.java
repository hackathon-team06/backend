package com.likelion.staycare.domain.mission.dto.response;

import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "날짜별 미션 조회 응답")
public record MissionByDateResponse(
        @Schema(description = "미션 ID", example = "1")
        Long missionId,

        @Schema(description = "미션 시간대", example = "MORNING")
        MissionTime missionTime,

        @Schema(description = "미션 제목", example = "아침 루틴")
        String title,

        @Schema(description = "미션 완료 여부", example = "false")
        boolean completed,

        @Schema(description = "미션 단계 목록")
        List<MissionStepDetailResponse> steps
) {
}
