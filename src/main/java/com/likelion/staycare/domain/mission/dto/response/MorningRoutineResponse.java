package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "아침 루틴 응답")
public record MorningRoutineResponse(
        Long routineId,
        List<MorningRoutineItemResponse> items
) {
}
