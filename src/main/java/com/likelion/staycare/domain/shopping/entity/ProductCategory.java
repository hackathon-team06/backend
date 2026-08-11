package com.likelion.staycare.domain.shopping.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@Schema(
        description = "상품 카테고리",
        example = "SKIN_TONER",
        allowableValues = {
                "SKIN_TONER", "ESSENCE_AMPOULE", "CREAM", "MASK_PACK", "SUPPLEMENT",
                "스킨/토너", "에센스/앰플", "크림", "마스크팩", "영양제"
        }
)
public enum ProductCategory {
    SKIN_TONER("스킨/토너"),
    ESSENCE_AMPOULE("에센스/앰플"),
    CREAM("크림"),
    MASK_PACK("마스크팩"),
    SUPPLEMENT("영양제");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static ProductCategory from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 상품 카테고리입니다: " + value));
    }
}
