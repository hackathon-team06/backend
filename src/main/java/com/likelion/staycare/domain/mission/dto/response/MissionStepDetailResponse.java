package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "미션 단계 상세 응답")
public record MissionStepDetailResponse(
        @Schema(description = "미션 단계 ID", example = "1")
        Long stepId,

        @Schema(description = "미션 단계 순서", example = "1")
        int stepOrder,

        @Schema(description = "미션 단계 내용", example = "세안하기")
        String content,

        @Schema(description = "단계 완료 여부", example = "false")
        boolean completed
) {
}
