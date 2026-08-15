package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "최초 생활 루틴 설문 선택지 응답")
public record MorningRoutineSurveyOptionsResponse(
        @Schema(description = "설문 코드와 한글 라벨 목록")
        List<MissionOptionItemResponse> items,

        @Schema(description = "한 번에 선택 가능한 최대 개수", example = "3")
        int maxSelections
) {
}
