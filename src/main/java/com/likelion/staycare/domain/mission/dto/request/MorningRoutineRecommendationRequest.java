package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "아침 고정 루틴 추천 요청")
public record MorningRoutineRecommendationRequest(
        @Size(max = 3)
        @ArraySchema(
                schema = @Schema(
                        description = "최대 3개까지 선택 가능한 아침 루틴 카테고리",
                        allowableValues = {
                                "MOISTURE",
                                "SUN_PROTECTION",
                                "CLEANSING",
                                "DIET_NUTRITION",
                                "SOOTHING_BARRIER",
                                "SLEEP_REST",
                                "HYGIENE",
                                "EXERCISE_STRETCHING",
                                "POPULAR",
                                "수분/보습",
                                "자외선 차단",
                                "세안/클렌징",
                                "식습관/영양",
                                "진정/장벽",
                                "수면/휴식",
                                "위생 관리",
                                "운동/스트레칭",
                                "인기 미션"
                        }
                )
        )
        List<MorningMissionCategory> categories
) {
}
