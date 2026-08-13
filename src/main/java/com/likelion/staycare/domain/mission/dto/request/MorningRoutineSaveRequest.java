package com.likelion.staycare.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "아침 루틴 저장 요청")
public record MorningRoutineSaveRequest(
        @Valid
        @NotEmpty
        List<MorningRoutineItemRequest> items
) {
}
