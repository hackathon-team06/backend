package com.likelion.staycare.domain.shopping.exception;

import com.likelion.staycare.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ShoppingErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND("SHOPPING4041", "해당 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_LIKE_NOT_FOUND("SHOPPING4042", "해당 찜 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE("SHOPPING4001", "비활성 상품입니다.", HttpStatus.BAD_REQUEST),
    PRODUCT_LIKE_ALREADY_EXISTS("SHOPPING4091", "이미 찜한 상품입니다.", HttpStatus.CONFLICT),
    SKIN_TYPE_REQUIRED("SHOPPING4002", "자가진단 후 피부 타입을 설정해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_SKIN_TYPE("SHOPPING4003", "지원하지 않는 피부 타입입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_CATEGORY("SHOPPING4004", "지원하지 않는 상품 카테고리입니다.", HttpStatus.BAD_REQUEST),
    INVALID_USE_POINTS("SHOPPING4005", "포인트 사용 값은 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
