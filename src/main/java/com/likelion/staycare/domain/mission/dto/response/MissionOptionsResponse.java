package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(
        description = """
                Mission 공통 옵션 응답입니다.
                아침 미션 추천과 저녁 미션 재추천에서 공통으로 사용하는 category 목록,
                그리고 저녁 상태 선택 코드를 한 번에 확인할 수 있습니다.
                """
)
public record MissionOptionsResponse(
        @Schema(description = "아침 미션 추천 및 저녁 미션 재추천에서 공통으로 사용하는 category 목록")
        List<MissionOptionItemResponse> morningCategories,

        @Schema(description = "저녁 상태 입력에 사용하는 상태 코드 목록")
        List<MissionOptionItemResponse> eveningConditions
) {
}
