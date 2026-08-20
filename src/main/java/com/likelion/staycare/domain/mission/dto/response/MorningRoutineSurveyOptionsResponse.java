package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "AI 추천 대체용 생활 루틴 후보 7개 조회 응답")
public record MorningRoutineSurveyOptionsResponse(
        @Schema(description = "생활 루틴 후보 코드와 라벨 목록")
        List<MissionOptionItemResponse> items,

        @Schema(description = "최종 고정 아침 미션 최대 개수", example = "3")
        int maxSelections
) {
}
