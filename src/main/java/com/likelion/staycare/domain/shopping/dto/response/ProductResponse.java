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

        @Schema(description = "상품명", example = "그린 티 수딩 토너")
        String name,

        @Schema(description = "브랜드명", example = "브랜드명")
        String brand,

        @Schema(description = "상품 카테고리", example = "스킨/토너")
        ProductCategory category,

        @Schema(description = "추천 피부 타입 목록")
        Set<SkinType> skinTypes,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "제휴사 판매 URL", example = "https://example.com/product")
        String purchaseUrl,

        @Schema(description = "판매 가격", example = "18900")
        Integer price,

        @Schema(description = "원래 가격", example = "27000", nullable = true)
        Integer originalPrice,

        @Schema(description = "할인율", example = "30", nullable = true)
        Integer discountRate,

        @Schema(description = "가격 갱신 시각")
        LocalDateTime priceUpdatedAt,

        @Schema(description = "현재 사용자의 찜 여부", example = "false")
        Boolean liked
) {
    public static ProductResponse from(Product product, boolean liked) {
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
                .build();
    }
}
