package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "미션 옵션 목록 응답")
public record MissionOptionsResponse(
        @Schema(description = "아침 루틴 카테고리 목록")
        List<MissionOptionItemResponse> morningCategories,

        @Schema(description = "저녁 귀가 후 상태 목록")
        List<MissionOptionItemResponse> eveningConditions
) {
}
