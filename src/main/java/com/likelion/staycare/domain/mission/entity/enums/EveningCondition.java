package com.likelion.staycare.domain.mission.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                저녁 피부/생활 상태 코드입니다.
                RED_HOT=붉고 뜨거움
                DRY_TIGHT=바짝 마르고 당김
                AC_LONG_EXPOSURE=히터/에어컨 장시간 노출
                STICKY_OILY=칙칙하고 푸석함
                SLEEP_LACK=수면 부족/피로
                DRINKING_DINING=음주/회식 진행함
                TROUBLE_OIL=트러블/유분 발생
                COLD_SENSITIVE=따갑고 민감함
                LONG_MAKEUP=메이크업 장시간 유지
                NONE=해당사항 없음, 반드시 단독 선택
                """
)
public enum EveningCondition {

    RED_HOT("붉고 뜨거움"),
    DRY_TIGHT("바짝 마르고 당김"),
    AC_LONG_EXPOSURE("히터/에어컨 장시간 노출"),
    STICKY_OILY("칙칙하고 푸석함"),
    SLEEP_LACK("수면 부족/피로"),
    DRINKING_DINING("음주/회식 진행함"),
    TROUBLE_OIL("트러블/유분 발생"),
    COLD_SENSITIVE("따갑고 민감함"),
    LONG_MAKEUP("메이크업 장시간 유지"),
    NONE("해당사항 없음");

    private final String label;

    EveningCondition(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static EveningCondition from(String value) {
        for (EveningCondition condition : values()) {
            if (condition.name().equalsIgnoreCase(value) || condition.label.equals(value)) {
                return condition;
            }
        }

        throw new IllegalArgumentException("Unknown evening condition: " + value);
    }
}
