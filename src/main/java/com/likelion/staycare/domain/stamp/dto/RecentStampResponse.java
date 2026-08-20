package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "최근 스탬프북 카드 정보")
public record RecentStampResponse(

        @Schema(description = "스탬프북 ID", example = "12")
        Long stampBookId,

        @Schema(description = "시작일", example = "2026-05-01")
        LocalDate startDate,

        @Schema(description = "종료일", example = "2026-05-28")
        LocalDate endDate,

        @Schema(description = "상태", example = "COMPLETED", allowableValues = {
                "IN_PROGRESS", "COMPLETED", "EXPIRED"
        })
        String status,

        @Schema(description = "화면 표시용 라벨", example = "28DAY")
        String label
) {
}
