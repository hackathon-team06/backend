package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = """
                저녁 미션 재추천 요청입니다.
                삭제 후 부족한 저녁 미션을 AI로 다시 추천받습니다.
                카테고리는 최대 2개까지 선택할 수 있으며 추천 개수는 카테고리 개수가 아니라
                현재 비어 있는 저녁 미션 슬롯 개수에 따라 결정됩니다.
                """
)
public record EveningMissionRecommendationRequest(
        @Size(max = 2)
        @ArraySchema(
                schema = @Schema(
                        description = """
                                저녁 미션 재추천에 우선 반영할 카테고리 코드입니다. 최대 2개까지 선택할 수 있습니다.
                                """,
                        example = "MOISTURE",
                        allowableValues = {
                                "MOISTURE",
                                "SUN_PROTECTION",
                                "CLEANSING",
                                "DIET_NUTRITION",
                                "SOOTHING_BARRIER",
                                "SLEEP_REST",
                                "HYGIENE",
                                "EXERCISE_STRETCHING",
                                "POPULAR"
                        }
                ),
                arraySchema = @Schema(
                        description = "비워 두면 AI가 현재 사용자 상태를 바탕으로 알아서 추천합니다.",
                        example = "[\"MOISTURE\", \"SOOTHING_BARRIER\"]"
                )
        )
        List<MorningMissionCategory> categories
) {
}
