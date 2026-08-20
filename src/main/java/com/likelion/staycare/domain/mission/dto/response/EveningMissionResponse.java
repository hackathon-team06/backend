package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                오늘 생성된 저녁 GeneratedMission 응답입니다.
                stepIds와 steps는 같은 순서로 대응하며, step 완료 API에는 stepId를 사용합니다.
                """
)
public record EveningMissionResponse(
        @Schema(description = "오늘 저녁 GeneratedMission 제목", example = "오늘 저녁 회복 미션")
        String title,

        @Schema(description = "오늘 저녁 GeneratedMission 설명", example = "오늘 저녁 피부와 컨디션을 고려한 회복 루틴입니다.")
        String description,

        @ArraySchema(schema = @Schema(description = "완료 API에서 사용하는 GeneratedMissionStep ID", example = "201"))
        List<Long> stepIds,

        @ArraySchema(schema = @Schema(description = "stepIds와 같은 순서의 step 문구", example = "미온수 세안 후 자극 없는 보습제를 충분히 바르기"))
        List<String> steps
) {
}
