package com.likelion.staycare.domain.shopping.dto.response;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Schema(description = "상품 응답")
public record ProductResponse(
        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품명", example = "레드 블레미쉬 클리어 수딩 토너")
        String name,

        @Schema(description = "브랜드명", example = "닥터지")
        String brand,

        @Schema(description = "상품 카테고리", example = "SKIN_TONER")
        ProductCategory category,

        @Schema(description = "추천 피부 타입 목록")
        Set<SkinType> skinTypes,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "제휴사 판매 URL", example = "https://example.com/product")
        String purchaseUrl,

        @Schema(description = "제휴사 할인 적용 후 판매가", example = "23900")
        Integer price,

        @Schema(description = "제휴사 정가", example = "32000", nullable = true)
        Integer originalPrice,

        @Schema(description = "제휴사 자체 할인율", example = "25", nullable = true)
        Integer discountRate,

        @Schema(description = "가격 갱신 시각")
        LocalDateTime priceUpdatedAt,

        @Schema(description = "현재 사용자의 찜 여부", example = "false")
        Boolean liked,

        @Schema(description = "현재 로그인 사용자의 전체 보유 포인트", example = "1500")
        Integer availablePoints,

        @Schema(description = "이 상품 가격에 실제 적용 가능한 포인트", example = "1500")
        Integer appliedPoints,

        @Schema(description = "포인트 적용 할인 금액", example = "1500")
        Integer pointDiscountAmount,

        @Schema(description = "현재 사용자 포인트 전체 적용 시 예상 가격", example = "22400")
        Integer pointAppliedPrice
) {
    public static ProductResponse from(Product product, boolean liked, int availablePoints) {
        int appliedPoints = Math.min(availablePoints, product.getPrice());
        int pointAppliedPrice = product.getPrice() - appliedPoints;

        return ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .skinTypes(product.getSkinTypes())
                .imageUrl(product.getImageUrl())
                .purchaseUrl(product.getPurchaseUrl())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .priceUpdatedAt(product.getPriceUpdatedAt())
                .liked(liked)
                .availablePoints(availablePoints)
                .appliedPoints(appliedPoints)
                .pointDiscountAmount(appliedPoints)
                .pointAppliedPrice(pointAppliedPrice)
                .build();
    }
}
