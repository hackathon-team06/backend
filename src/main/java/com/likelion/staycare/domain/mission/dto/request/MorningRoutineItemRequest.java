package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = """
                고정 아침 미션 1개 저장 객체입니다.
                items 배열 안에는 반드시 이 객체 구조가 들어가야 하며, 문자열 배열을 보내면 안 됩니다.
                AI 추천 결과를 선택했다면 source=AI, 사용자가 직접 작성했다면 source=CUSTOM을 사용합니다.
                """
)
public record MorningRoutineItemRequest(
        @NotBlank
        @Schema(
                description = "실제로 매일 반복 수행할 아침 미션 문구",
                example = "세안 후 3분 안에 보습제를 충분히 발라 수분을 잠그기",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String content,

        @Schema(
                description = """
                        미션 category 코드입니다.
                        AI 추천 미션이면 보통 category를 함께 보냅니다.
                        CUSTOM 미션은 null 허용입니다.

                        허용 코드:
                        MOISTURE=수분/보습
                        SUN_PROTECTION=자외선 차단
                        CLEANSING=세안/클렌징
                        DIET_NUTRITION=식습관/영양
                        SOOTHING_BARRIER=진정/장벽
                        SLEEP_REST=수면/휴식
                        HYGIENE=위생 관리
                        EXERCISE_STRETCHING=운동/스트레칭
                        POPULAR=인기 미션
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
                        AI=AI 추천 결과에서 사용자가 선택한 미션
                        CUSTOM=사용자가 직접 작성한 미션

                        SURVEY는 최초 생활 루틴 설문 전용 개념이므로 이 API 요청에는 사용하지 않습니다.
                        """,
                example = "AI",
                allowableValues = {"AI", "CUSTOM"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        MorningRoutineItemSource source
) {
}
