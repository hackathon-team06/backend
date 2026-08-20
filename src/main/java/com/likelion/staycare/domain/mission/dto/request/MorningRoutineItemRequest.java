package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = """
                최종 고정 아침 미션 1개 저장 객체입니다.
                source는 AI 추천 선택, 생활 루틴 후보 선택, 직접 입력 중 하나입니다.
                """
)
public record MorningRoutineItemRequest(
        @NotBlank
        @Schema(
                description = "매일 반복 수행할 고정 아침 미션 문구",
                example = "세안 후 3분 안에 보습제를 충분히 바르기",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String content,

        @Schema(
                description = """
                        미션 category 코드입니다.
                        AI 추천 미션이면 category를 함께 보내는 것을 권장합니다.
                        SURVEY 또는 CUSTOM 항목은 null 허용입니다.

                        사용 코드:
                        MOISTURE = 수분/보습
                        SUN_PROTECTION = 자외선 차단
                        CLEANSING = 세안/클렌징
                        DIET_NUTRITION = 식습관/영양
                        SOOTHING_BARRIER = 진정/장벽
                        SLEEP_REST = 수면/휴식
                        HYGIENE = 위생 관리
                        EXERCISE_STRETCHING = 운동/스트레칭
                        POPULAR = 인기 미션
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
                },
                nullable = true
        )
        MorningMissionCategory category,

        @NotNull
        @Schema(
                description = """
                        미션 출처 코드입니다.
                        AI = AI 추천 결과
                        SURVEY = 생활 루틴 후보 7개 중 선택
                        CUSTOM = 사용자 직접 입력
                        """,
                example = "AI",
                allowableValues = {"AI", "SURVEY", "CUSTOM"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        MorningRoutineItemSource source
) {
}
