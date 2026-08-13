package com.likelion.staycare.domain.shopping.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "포인트 적용 예상 가격 응답")
public record PointPriceResponse(
        @Schema(description = "상품 ID", example = "17")
        Long productId,

        @Schema(description = "제휴사 정가", example = "39900", nullable = true)
        Integer originalPrice,

        @Schema(description = "제휴사 할인 적용 후 현재 판매가", example = "29900")
        Integer price,

        @Schema(description = "현재 사용자의 보유 포인트", example = "1163")
        Integer availablePoints,

        @Schema(description = "사용자가 적용 요청한 포인트", example = "1000")
        Integer requestedPoints,

        @Schema(description = "실제 계산에 적용된 포인트", example = "1000")
        Integer usedPoints,

        @Schema(description = "포인트 할인 금액", example = "1000")
        Integer pointDiscountAmount,

        @Schema(description = "포인트 적용 예상 가격", example = "28900")
        Integer pointAppliedPrice
) {
}
