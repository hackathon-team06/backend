package com.likelion.staycare.domain.mission.dto.request;

import com.likelion.staycare.domain.mission.entity.enums.EveningCondition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Schema(
        description = """
                저녁 미션 생성 전 현재 피부/생활 상태 입력입니다.
                프론트는 conditions 배열만 보내면 되며, noneExclusive 같은 별도 boolean은 보내지 않습니다.
                NONE은 단독 선택만 가능합니다.
                """
)
public record EveningMissionCreateRequest(
        @NotEmpty
        @ArraySchema(
                schema = @Schema(
                        description = """
                                저녁 상태 코드입니다.

                                RED_HOT=붉고 뜨거움
                                DRY_TIGHT=바짝 마르고 당김
                                AC_LONG_EXPOSURE=히터/에어컨 장시간 노출
                                STICKY_OILY=칙칙하고 푸석함
                                SLEEP_LACK=수면 부족/피로
                                DRINKING_DINING=음주/회식 진행함
                                TROUBLE_OIL=트러블/유분 발생
                                COLD_SENSITIVE=따갑고 민감함
                                LONG_MAKEUP=메이크업 장시간 유지
                                NONE=해당사항 없음
                                """,
                        example = "RED_HOT",
                        allowableValues = {
                                "RED_HOT",
                                "DRY_TIGHT",
                                "AC_LONG_EXPOSURE",
                                "STICKY_OILY",
                                "SLEEP_LACK",
                                "DRINKING_DINING",
                                "TROUBLE_OIL",
                                "COLD_SENSITIVE",
                                "LONG_MAKEUP",
                                "NONE"
                        }
                ),
                arraySchema = @Schema(
                        description = "복수 선택 가능. 단, NONE은 단독 선택만 가능",
                        example = "[\"RED_HOT\", \"DRY_TIGHT\"]"
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
