package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "주간 미션 완료 현황의 하루 단위 응답")
public record WeeklyMissionDayResponse(
        @Schema(description = "해당 날짜", example = "2026-07-27")
        LocalDate date,

        @Schema(
                description = "아침(MORNING)과 저녁(EVENING) GeneratedMission이 모두 COMPLETED이면 true, 그 외는 false",
                example = "true"
        )
        boolean completed
) {
}
