package com.likelion.staycare.domain.stamp.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "마이페이지 스탬프 요약")
public record StampResponse(

        @Schema(description = "완료한 스탬프북 개수", example = "2")
        Integer totalCompletedStampBooks,

        @Schema(description = "현재 진행 중인 스탬프북 ID", example = "13", nullable = true)
        Long currentStampBookId,

        @Schema(description = "현재 진행 일수", example = "18")
        Integer currentProgressDays,

        @Schema(description = "전체 일수", example = "28")
        Integer currentTotalDays,

        @Schema(description = "최근 스탬프북 목록")
        List<RecentStampResponse> recentStampBooks
) {
}
