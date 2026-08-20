package com.likelion.staycare.domain.stamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "마이페이지 스탬프 요약 응답")
public record MyPageStampSummaryResponse(

        @Schema(description = "전체 스탬프북 카드 개수 (진행중 + 완료 포함)", example = "2")
        Integer totalStampBookCount,

        @Schema(description = "완료된 스탬프북 카드 개수", example = "1")
        Integer completedStampBookCount,

        @Schema(description = "스탬프북 카드 목록")
        List<StampBookCardResponse> stampBooks
) {
}
