package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "AI가 다시 추천한 저녁 미션 후보 응답")
public record EveningMissionRecommendationResponse(
        @ArraySchema(
                schema = @Schema(
                        description = "AI가 다시 추천한 저녁 미션 문구",
                        example = "세안 후 자극 없는 보습제를 충분히 바르기"
                ),
                arraySchema = @Schema(description = "현재 비어 있는 저녁 미션 슬롯 개수만큼 반환되는 추천 목록")
        )
        List<String> recommendations
) {
}
