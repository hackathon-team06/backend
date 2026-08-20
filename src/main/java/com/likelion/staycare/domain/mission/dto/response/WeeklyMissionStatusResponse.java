package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
@Schema(description = "기준 날짜가 포함된 월요일부터 일요일까지의 주간 미션 완료 현황 응답")
public record WeeklyMissionStatusResponse(
        @Schema(description = "조회 범위 시작일(월요일)", example = "2026-07-27")
        LocalDate startDate,

        @Schema(description = "조회 범위 종료일(일요일)", example = "2026-08-02")
        LocalDate endDate,

        @ArraySchema(schema = @Schema(implementation = WeeklyMissionDayResponse.class))
        List<WeeklyMissionDayResponse> days
) {
}
