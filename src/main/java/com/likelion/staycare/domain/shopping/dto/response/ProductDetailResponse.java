package com.likelion.staycare.domain.shopping.dto.response;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.entity.Product;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@Schema(description = "상품 상세 응답")
public record ProductDetailResponse(
        @Schema(description = "상품 ID", example = "17")
        Long productId,

        @Schema(description = "상품명", example = "레드 블레미쉬 클리어 수딩 크림")
        String name,

        @Schema(description = "브랜드명", example = "닥터지")
        String brand,

        @Schema(description = "상품 카테고리", example = "CREAM")
        ProductCategory category,

        @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @Schema(description = "구매 이동 URL", example = "https://example.com/product")
        String purchaseUrl,

        @Schema(description = "현재 판매가", example = "28500")
        Integer price,

        @Schema(description = "정가", example = "38000", nullable = true)
        Integer originalPrice,

        @Schema(description = "할인율", example = "25", nullable = true)
        Integer discountRate,

        @Schema(description = "평점", example = "5.0", nullable = true)
        BigDecimal rating,

        @Schema(description = "리뷰 수", example = "3306")
        Integer reviewCount,

        @Schema(description = "현재 로그인 사용자의 찜 여부", example = "true")
        Boolean liked,

        @Schema(description = "추천 피부타입 목록")
        Set<SkinType> skinTypes
) {
    public static ProductDetailResponse from(Product product, boolean liked) {
        return ProductDetailResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .imageUrl(product.getImageUrl())
                .purchaseUrl(product.getPurchaseUrl())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .discountRate(product.getDiscountRate())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .liked(liked)
                .skinTypes(product.getSkinTypes())
                .build();
    }
}
