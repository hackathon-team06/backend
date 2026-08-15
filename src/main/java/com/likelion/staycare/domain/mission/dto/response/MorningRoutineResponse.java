package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                현재 사용자가 매일 수행하도록 확정한 고정 아침 미션 응답입니다.
                최초 설문 데이터는 포함되지 않으며, 실제 MorningRoutineItem만 반환합니다.
                routineId는 고정 아침 루틴 묶음 ID이고, itemId는 개별 미션 삭제에 사용합니다.
                """
)
public record MorningRoutineResponse(
        @Schema(description = "고정 아침 루틴 묶음 ID", example = "1")
        Long routineId,

        @ArraySchema(schema = @Schema(implementation = MorningRoutineItemResponse.class))
        List<MorningRoutineItemResponse> items
) {
}
