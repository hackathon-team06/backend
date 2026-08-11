package com.likelion.staycare.domain.shopping.dto.request;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.Set;

@Schema(name = "ProductCreateRequest", description = "제휴 상품 등록 요청")
public record ProductCreateRequest(
        @NotBlank(message = "상품명을 입력해주세요.")
        @Schema(description = "상품명", example = "그린 티 수딩 토너")
        String name,

        @Schema(description = "브랜드명", example = "브랜드명")
        String brand,

        @NotNull(message = "상품 카테고리를 입력해주세요.")
        @Schema(
                description = "상품 카테고리",
                example = "SKIN_TONER",
                allowableValues = {
                        "SKIN_TONER", "ESSENCE_AMPOULE", "CREAM", "MASK_PACK", "SUPPLEMENT"
                }
        )
        ProductCategory category,

        @NotEmpty(message = "추천 피부 타입을 하나 이상 선택해주세요.")
        @Schema(description = "추천 피부 타입 목록")
        Set<SkinType> skinTypes,

        @NotBlank(message = "상품 이미지 URL을 입력해주세요.")
        @Schema(description = "상품 이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,

        @NotBlank(message = "상품 판매 URL을 입력해주세요.")
        @Schema(description = "제휴사 상품 판매 URL", example = "https://example.com/product")
        String purchaseUrl,

        @NotNull(message = "판매 가격을 입력해주세요.")
        @PositiveOrZero(message = "판매 가격은 음수일 수 없습니다.")
        @Schema(description = "판매 가격", example = "18900")
        Integer price,

        @PositiveOrZero(message = "원래 가격은 음수일 수 없습니다.")
        @Schema(description = "원래 가격", example = "27000", nullable = true)
        Integer originalPrice,

        @PositiveOrZero(message = "할인율은 음수일 수 없습니다.")
        @Schema(description = "할인율", example = "30", nullable = true)
        Integer discountRate
) {
}
