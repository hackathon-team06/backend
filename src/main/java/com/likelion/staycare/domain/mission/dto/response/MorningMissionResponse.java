package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                오늘 생성된 아침 GeneratedMission 응답입니다.
                stepIds와 steps는 같은 순서로 대응하며, step 완료 API에는 stepId를 사용합니다.
                """
)
public record MorningMissionResponse(
        @Schema(description = "오늘 아침 GeneratedMission 제목", example = "오늘의 아침 미션")
        String title,

        @Schema(description = "오늘 아침 GeneratedMission 설명", example = "확정한 고정 아침 미션을 오늘 아침 실천할 수 있도록 생성한 미션입니다.")
        String description,

        @ArraySchema(schema = @Schema(description = "완료 API에서 사용하는 GeneratedMissionStep ID", example = "101"))
        List<Long> stepIds,

        @ArraySchema(schema = @Schema(description = "stepIds와 같은 순서의 step 문구", example = "세안 후 3분 안에 보습제를 충분히 바르기"))
        List<String> steps
) {
}
