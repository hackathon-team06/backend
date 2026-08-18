package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "월별 스탬프 달력 응답")
public record StampCalendarResponse(

        @Schema(description = "조회 연도", example = "2026")
        Integer year,

        @Schema(description = "조회 월", example = "8")
        Integer month,

        @Schema(description = "월별 요약")
        StampCalendarSummaryResponse summary,

        @Schema(description = "일별 스탬프 목록")
        List<StampCalendarDayResponse> days
) {
}
