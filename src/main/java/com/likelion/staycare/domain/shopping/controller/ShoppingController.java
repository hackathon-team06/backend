package com.likelion.staycare.domain.shopping.controller;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.shopping.dto.request.ProductCreateRequest;
import com.likelion.staycare.domain.shopping.dto.response.ProductResponse;
import com.likelion.staycare.domain.shopping.entity.ProductCategory;
import com.likelion.staycare.domain.shopping.service.ShoppingService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductCreateRequest request
    ) {
        return ResponseEntity.ok(shoppingService.createProduct(request));
    }

    @Operation(
            summary = "상품 조회",
            description = """
                    피부 타입과 카테고리 기준으로 제휴 상품을 조회합니다.
                    skinType 미입력: 현재 로그인 사용자의 피부 타입 사용
                    category 미입력: SKIN_TONER 사용
                    """
    )
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) SkinType skinType,
            @RequestParam(required = false) ProductCategory category
    ) {
        return ResponseEntity.ok(
                shoppingService.getProducts(userDetails.getUserId(), skinType, category)
        );
    }

    @Operation(summary = "상품 찜", description = "현재 로그인 사용자가 상품을 찜합니다.")
    @PostMapping("/products/{productId}/likes")
    public ResponseEntity<Void> likeProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        shoppingService.likeProduct(userDetails.getUserId(), productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 찜 취소", description = "현재 로그인 사용자가 상품 찜을 취소합니다.")
    @DeleteMapping("/products/{productId}/likes")
    public ResponseEntity<Void> unlikeProduct(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        shoppingService.unlikeProduct(userDetails.getUserId(), productId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내가 찜한 상품 목록 조회", description = "현재 로그인 사용자의 찜 목록을 조회합니다. category 미입력 시 전체를 반환합니다.")
    @GetMapping("/likes")
    public ResponseEntity<List<ProductResponse>> getLikedProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) ProductCategory category
    ) {
        return ResponseEntity.ok(
                shoppingService.getLikedProducts(userDetails.getUserId(), category)
        );
    }
}
