package com.likelion.staycare.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = """
                실제 고정 아침 미션 저장 요청
                초기 확정 시에는 3개를 전달하고, 기존 미션 삭제 후 보충할 때는 부족한 개수만큼만 전달합니다.
                전달된 항목은 모두 실제 매일 반복 사용할 고정 아침 미션으로 저장됩니다.
                """
)
public record MorningRoutineSaveRequest(
        @Valid
        @NotEmpty
        @Size(min = 1, max = 3)
        @ArraySchema(schema = @Schema(description = "초기 확정 3개 또는 삭제 후 부족한 개수만큼의 고정 아침 미션"))
        List<MorningRoutineItemRequest> items
) {
}
