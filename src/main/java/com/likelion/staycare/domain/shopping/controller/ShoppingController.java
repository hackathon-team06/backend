package com.likelion.staycare.domain.shopping.controller;

import com.likelion.staycare.domain.shopping.dto.request.ProductCreateRequest;
import com.likelion.staycare.domain.shopping.dto.response.PointPriceResponse;
import com.likelion.staycare.domain.shopping.dto.response.ProductResponse;
import com.likelion.staycare.domain.shopping.service.ShoppingService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Shopping", description = "쇼핑 API")
@RestController
@RequestMapping("/api/shopping")
@RequiredArgsConstructor
public class ShoppingController {

    private final ShoppingService shoppingService;

    @Operation(summary = "제휴 상품 등록", description = "Swagger에서 제휴 상품 정보를 직접 입력하여 등록합니다.")
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        return ResponseEntity.ok(shoppingService.createProduct(request));
    }

    @Operation(
            summary = "상품 조회",
            description = """
                    피부타입과 카테고리에 맞는 상품 목록을 조회합니다.

                    각 상품 응답에는 현재 로그인 사용자의 보유 포인트를
                    1P=1원 기준으로 현재 판매가(price)에 전부 적용했을 때의
                    예상 가격이 포함됩니다.

                    포인트는 실제로 차감되지 않습니다.
                    """
    )
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    description = "피부 타입 필터. 영문 enum 또는 한글명을 사용할 수 있습니다.",
                    schema = @Schema(
                            type = "string",
                            example = "DEHYDRATED",
                            allowableValues = {
                                    "DRY", "OILY", "COMBINATION", "DEHYDRATED", "NORMAL",
                                    "건성", "지성", "복합성", "수부지", "중성"
                            }
                    )
            )
            @RequestParam(required = false) String skinType,
            @Parameter(
                    description = "상품 카테고리. 영문 enum 또는 한글명을 사용할 수 있습니다.",
                    schema = @Schema(
                            type = "string",
                            example = "SKIN_TONER",
                            allowableValues = {
                                    "SKIN_TONER", "ESSENCE_AMPOULE", "CREAM", "MASK_PACK", "SUPPLEMENT",
                                    "스킨/토너", "에센스/앰플", "크림", "마스크팩", "영양제"
                            }
                    )
            )
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(
                shoppingService.getProducts(userDetails.getUserId(), skinType, category)
        );
    }

    @Operation(
            summary = "랜덤 추천 상품 5개 조회",
            description = """
                    DB에 저장된 활성 상품 중 랜덤으로 최대 5개를 반환합니다.

                    - 동일 상품은 한 응답 안에서 중복되지 않습니다.
                    - 상품이 5개 미만이면 존재하는 상품만 반환합니다.
                    - 상품이 없으면 빈 배열([])을 반환합니다.
                    - 실제 구매량, 인기순, 연령대 통계 기반 추천이 아닙니다.
                    - 호출할 때마다 결과가 달라질 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "랜덤 추천 상품 조회 성공",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)),
                            examples = @ExampleObject(
                                    name = "randomProducts",
                                    value = """
                                            [
                                              {
                                                "productId": 12,
                                                "name": "히알루론산 수분 토너",
                                                "brand": "브랜드명",
                                                "category": "SKIN_TONER",
                                                "skinTypes": ["DRY", "DEHYDRATED"],
                                                "imageUrl": "https://example.com/image.jpg",
                                                "purchaseUrl": "https://example.com/product",
                                                "price": 18000,
                                                "originalPrice": 22000,
                                                "discountRate": 18,
                                                "priceUpdatedAt": "2026-08-16T09:30:00",
                                                "liked": false,
                                                "availablePoints": 1500,
                                                "appliedPoints": 1500,
                                                "pointDiscountAmount": 1500,
                                                "pointAppliedPrice": 16500
                                              },
                                              {
                                                "productId": 3,
                                                "name": "판테놀 장벽 크림",
                                                "brand": "브랜드명",
                                                "category": "CREAM",
                                                "skinTypes": ["DRY", "NORMAL"],
                                                "imageUrl": "https://example.com/image2.jpg",
                                                "purchaseUrl": "https://example.com/product2",
                                                "price": 23000,
                                                "originalPrice": 28000,
                                                "discountRate": 17,
                                                "priceUpdatedAt": "2026-08-16T09:30:00",
                                                "liked": true,
                                                "availablePoints": 1500,
                                                "appliedPoints": 1500,
                                                "pointDiscountAmount": 1500,
                                                "pointAppliedPrice": 21500
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/products/random")
    public ResponseEntity<List<ProductResponse>> getRandomProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                shoppingService.getRandomProducts(userDetails.getUserId())
        );
    }

    @Operation(summary = "상품 찜", description = "현재 로그인한 사용자가 상품을 찜합니다.")
    @PostMapping("/products/{productId}/likes")
    public ResponseEntity<Void> likeProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        shoppingService.likeProduct(userDetails.getUserId(), productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 찜 취소", description = "현재 로그인한 사용자가 상품 찜을 취소합니다.")
    @DeleteMapping("/products/{productId}/likes")
    public ResponseEntity<Void> unlikeProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        shoppingService.unlikeProduct(userDetails.getUserId(), productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "제휴 상품 삭제", description = "활성 상태의 제휴 상품을 삭제 처리합니다.")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId
    ) {
        shoppingService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 찜한 상품 목록 조회", description = "현재 로그인한 사용자의 찜 목록을 조회합니다. category 미입력 시 전체를 반환합니다.")
    @GetMapping("/likes")
    public ResponseEntity<List<ProductResponse>> getLikedProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    description = "찜 목록 카테고리 필터. 영문 enum 또는 한글명을 사용할 수 있습니다.",
                    schema = @Schema(
                            type = "string",
                            example = "CREAM",
                            allowableValues = {
                                    "SKIN_TONER", "ESSENCE_AMPOULE", "CREAM", "MASK_PACK", "SUPPLEMENT",
                                    "스킨/토너", "에센스/앰플", "크림", "마스크팩", "영양제"
                            }
                    )
            )
            @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(
                shoppingService.getLikedProducts(userDetails.getUserId(), category)
        );
    }

    @Operation(
            summary = "포인트 적용 예상 가격 조회",
            description = """
                    현재 상품의 제휴사 할인 판매가(price)에
                    사용자의 포인트를 1P = 1원 기준으로 추가 적용했을 때의
                    예상 가격을 계산합니다.

                    실제 결제 및 포인트 차감은 수행하지 않습니다.
                    """
    )
    @GetMapping("/products/{productId}/point-price")
    public ResponseEntity<PointPriceResponse> getPointAppliedPrice(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @Parameter(
                    description = "적용해 볼 포인트 수",
                    schema = @Schema(type = "integer", example = "1000", minimum = "0")
            )
            @RequestParam Integer usePoints
    ) {
        return ResponseEntity.ok(
                shoppingService.getPointAppliedPrice(userDetails.getUserId(), productId, usePoints)
        );
    }
}
