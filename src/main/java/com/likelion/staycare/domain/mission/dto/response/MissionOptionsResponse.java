package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                Mission 공통 옵션 응답입니다.
                아침 고정 미션 category와 저녁 상태 선택 코드를 한 번에 확인할 수 있습니다.
                """
)
public record MissionOptionsResponse(
        @Schema(description = "아침 고정 미션 추천/저장 시 사용할 category 목록")
        List<MissionOptionItemResponse> morningCategories,

        @Schema(description = "저녁 상태 입력 시 사용할 상태 코드 목록")
        List<MissionOptionItemResponse> eveningConditions
) {
}
