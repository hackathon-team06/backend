package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "미션 선택 옵션 항목")
public record MissionOptionItemResponse(
        @Schema(description = "영문 enum 값", example = "MOISTURE")
        String code,

        @Schema(description = "한글 표시명", example = "수분/보습")
        String label
) {
}
