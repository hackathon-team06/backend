package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = """
                아침 고정 미션 AI 추천 요청입니다.
                최초 추천과 삭제 후 보충 추천 모두 이 DTO를 사용합니다.
                categories는 어떤 종류의 새 미션을 추천받고 싶은지 선택적으로 보내는 값입니다.
                """
)
public record MorningRoutineRecommendationRequest(
        @Size(max = 3)
        @ArraySchema(
                schema = @Schema(
                        description = """
                                원하는 아침 미션 category 코드입니다. 최대 3개까지 선택할 수 있습니다.

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
                        }
                ),
                arraySchema = @Schema(
                        description = "추천받고 싶은 미션 category 배열. 비워 두면 전체 범위에서 추천합니다.",
                        example = "[\"MOISTURE\", \"SUN_PROTECTION\"]"
                )
        )
        List<MorningMissionCategory> categories
) {
}
