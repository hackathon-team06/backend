package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                AI가 추천한 고정 아침 미션 후보 응답입니다.
                이 응답은 아직 DB에 저장된 상태가 아니며, 프론트가 사용자의 선택을 받아 고정 아침 미션 저장 API로 다시 보내야 합니다.
                """
)
public record MorningRoutineRecommendationResponse(
        @ArraySchema(
                schema = @Schema(
                        description = "AI 추천 미션 문구",
                        example = "세안 후 3분 안에 보습제를 충분히 바르기"
                ),
                arraySchema = @Schema(description = "현재 부족한 개수만큼 반환되는 AI 추천 후보 목록")
        )
        List<String> recommendations
) {
}
