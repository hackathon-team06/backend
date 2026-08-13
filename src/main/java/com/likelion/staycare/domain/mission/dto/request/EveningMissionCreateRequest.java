package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.EveningCondition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Schema(description = "저녁 미션 생성 전 귀가 후 상태 입력")
public record EveningMissionCreateRequest(
        @NotEmpty
        @ArraySchema(
                schema = @Schema(
                        description = "복수 선택 가능한 귀가 후 상태. NONE은 단독 선택만 허용",
                        allowableValues = {
                                "RED_HOT",
                                "DRY_TIGHT",
                                "AC_HEATER_EXPOSURE",
                                "DULL_ROUGH",
                                "SLEEP_DEPRIVED",
                                "DRINKING_DINING",
                                "TROUBLE_OIL",
                                "SENSITIVE_STINGING",
                                "LONG_MAKEUP",
                                "NONE",
                                "붉고 뜨거움",
                                "바짝 마르고 당김",
                                "히터/에어컨 장시간 노출",
                                "칙칙, 푸석함",
                                "수면 부족/피로",
                                "음주/회식 진행함",
                                "트러블/유분 발생",
                                "따갑고 민감함",
                                "메이크업 장시간 유지",
                                "해당사항 없음"
                        }
                )
        )
        Set<EveningCondition> conditions
) {
    @AssertTrue(message = "NONE은 다른 상태와 함께 선택할 수 없습니다.")
    public boolean isNoneExclusive() {
        return conditions == null
                || !conditions.contains(EveningCondition.NONE)
                || conditions.size() == 1;
    }
}
