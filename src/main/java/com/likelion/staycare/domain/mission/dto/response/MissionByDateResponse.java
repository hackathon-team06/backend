package com.likelion.staycare.domain.mission.dto.response;

import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "특정 날짜의 GeneratedMission 1건 응답")
public record MissionByDateResponse(
        @Schema(description = "GeneratedMission ID. step 상세 조회 API에서 missionId로 사용", example = "55")
        Long missionId,

        @Schema(description = "미션 시간대", example = "MORNING")
        MissionTime missionTime,

        @Schema(description = "미션 제목", example = "오늘의 아침 미션")
        String title,

        @Schema(description = "전체 미션 완료 여부", example = "false")
        boolean completed,

        @Schema(description = "해당 mission의 step 목록")
        List<MissionStepDetailResponse> steps
) {
}
