package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineSurveyOption;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(
        description = """
                최초 생활 루틴 설문 저장 요청입니다.
                설문은 최초 1회만 저장하며, 실제 고정 아침 미션 저장과는 다른 API입니다.
                items에는 설문 코드 1~3개를 중복 없이 전달합니다.
                """
)
public record MorningRoutineSurveySaveRequest(
        @NotEmpty
        @Size(max = 3)
        @ArraySchema(
                schema = @Schema(
                        description = """
                                최초 생활 루틴 설문 코드입니다.

                                WATER_AFTER_WAKEUP=기상 직후 미온수 한 잔 마시기
                                QUICK_WASH_AFTER_RETURN=귀가 후 10분 이내에 세안하기
                                NIGHT_STRETCHING=잠들기 전 스트레칭으로 혈액순환 돕기
                                TAKE_SKIN_SUPPLEMENT=피부 영양제 챙겨 먹기
                                CARRY_LIPBALM_SUNSTICK=립밤/선스틱 가방에 챙기기
                                MORNING_VENTILATION=아침 환기 5분 시키기
                                APPLY_SUNSCREEN_BEFORE_OUTING=외출 전 선크림 바르기
                                """,
                        example = "APPLY_SUNSCREEN_BEFORE_OUTING",
                        allowableValues = {
                                "WATER_AFTER_WAKEUP",
                                "QUICK_WASH_AFTER_RETURN",
                                "NIGHT_STRETCHING",
                                "TAKE_SKIN_SUPPLEMENT",
                                "CARRY_LIPBALM_SUNSTICK",
                                "MORNING_VENTILATION",
                                "APPLY_SUNSCREEN_BEFORE_OUTING"
                        }
                ),
                arraySchema = @Schema(
                        description = "최소 1개, 최대 3개. 최초 1회만 저장 가능",
                        example = "[\"APPLY_SUNSCREEN_BEFORE_OUTING\", \"NIGHT_STRETCHING\", \"WATER_AFTER_WAKEUP\"]"
                )
        )
        Set<MorningRoutineSurveyOption> items
) {
}
