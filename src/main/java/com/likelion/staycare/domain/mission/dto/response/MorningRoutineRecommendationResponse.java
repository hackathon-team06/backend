package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "아침 루틴 추천 응답")
public record MorningRoutineRecommendationResponse(
        @Schema(description = "추천 후보 목록")
        List<String> recommendations
) {
}
