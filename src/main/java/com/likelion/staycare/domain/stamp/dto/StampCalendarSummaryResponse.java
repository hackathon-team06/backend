package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "월별 스탬프 요약")
public record StampCalendarSummaryResponse(

        @Schema(description = "월간 총 완료 스탬프 수(= 완료한 step 수)", example = "42")
        Integer totalStampCount,

        @Schema(description = "월간 일일 포인트 합계(step 포인트 + 하루 완료 보너스)", example = "58")
        Integer dailyPoint,

        @Schema(description = "월간 정산 포인트 합계(STAMP_COMPLETION)", example = "20")
        Integer completionPoint,

        @Schema(description = "월간 총 획득 포인트", example = "78")
        Integer totalEarnedPoint
) {
}
