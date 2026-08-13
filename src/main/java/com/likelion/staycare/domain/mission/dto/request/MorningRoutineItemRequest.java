package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "아침 고정 루틴 항목 요청")
public record MorningRoutineItemRequest(
        @NotBlank
        @Schema(description = "루틴 내용", example = "수분 토너 사용하기")
        String content,

        @Schema(
                description = "선택한 카테고리. 직접 입력 항목이면 null 가능",
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
        MorningMissionCategory category,

        @NotNull
        @Schema(description = "항목 출처", allowableValues = {"AI", "CUSTOM"})
        MorningRoutineItemSource source
) {
}
