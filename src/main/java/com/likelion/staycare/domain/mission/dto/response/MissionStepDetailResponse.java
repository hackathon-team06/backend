package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "GeneratedMissionStep 상세 응답")
public record MissionStepDetailResponse(
        @Schema(description = "step 완료 API에서 사용하는 GeneratedMissionStep ID", example = "101")
        Long stepId,

        @Schema(description = "해당 미션 안에서의 step 순서", example = "1")
        int stepOrder,

        @Schema(description = "step 문구", example = "세안 후 3분 안에 보습제를 충분히 바르기")
        String content,

        @Schema(description = "현재 사용자의 해당 step 완료 여부", example = "false")
        boolean completed
) {
}
