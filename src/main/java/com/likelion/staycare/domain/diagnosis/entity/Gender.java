package com.likelion.staycare.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private final String label;

    @JsonValue
    public String getJsonValue() {
        return label;
    }

    @JsonCreator
    public static Gender from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("성별 값이 비어 있습니다.");
        }

        String normalized = value.trim();

        for (Gender gender : values()) {
            if (gender.label.equals(normalized) || gender.name().equalsIgnoreCase(normalized)) {
                return gender;
            }
        }

        throw new IllegalArgumentException("지원하지 않는 성별입니다: " + value);
    }
}
