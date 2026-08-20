package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineSurveyOption;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

@Schema(
        description = """
                Deprecated. 기존 생활 루틴 설문 저장 요청 DTO입니다.
                현재 메인 플로우에서는 사용하지 않으며, 생활 루틴 후보는 저장하지 않습니다.
                """
)
public record MorningRoutineSurveySaveRequest(
        @NotEmpty
        @Size(max = 3)
        @ArraySchema(
                schema = @Schema(
                        description = "기존 생활 루틴 설문 코드",
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
                        description = "Deprecated 요청 필드입니다.",
                        example = "[\"APPLY_SUNSCREEN_BEFORE_OUTING\", \"NIGHT_STRETCHING\"]"
                )
        )
        Set<MorningRoutineSurveyOption> items
) {
}
