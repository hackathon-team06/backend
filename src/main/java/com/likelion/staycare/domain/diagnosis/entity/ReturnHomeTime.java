package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReturnHomeTime {
    HOUR_6("6시"),
    HOUR_7("7시"),
    HOUR_8("8시"),
    HOUR_9("9시"),
    HOUR_10("10시"),
    HOUR_11("11시"),
    HOUR_12("12시");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static ReturnHomeTime from(String value) {
        return Arrays.stream(values())
                .filter(v -> v.label.equals(value) || v.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 귀가 시간입니다: " + value));
    }
}
