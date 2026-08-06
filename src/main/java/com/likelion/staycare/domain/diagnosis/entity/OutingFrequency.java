package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OutingFrequency {
    ONCE_A_WEEK("주 1회"),
    TWO_TO_THREE("주 2~3회"),
    FOUR_TO_FIVE("주 4~5회"),
    FIVE_PLUS("주 5회 이상");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static OutingFrequency from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 동기입니다: " + value));
    }
}

