package com.likelion.staycare.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "아침 루틴 추천 요청")
public record MorningRoutineRecommendationRequest(
        @Schema(description = "원하는 루틴 카테고리 목록", example = "[\"보습\", \"자외선 차단\"]")
        List<String> categories
) {
}
