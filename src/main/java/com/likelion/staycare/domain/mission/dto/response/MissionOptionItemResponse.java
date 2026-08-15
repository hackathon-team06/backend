package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Swagger와 옵션 조회 API에서 공통으로 사용하는 enum 코드/라벨 응답")
public record MissionOptionItemResponse(
        @Schema(description = "프론트가 요청에 그대로 사용하는 enum 코드", example = "MOISTURE")
        String code,

        @Schema(description = "사용자 화면과 Swagger에서 함께 보여 줄 한글 라벨", example = "수분/보습")
        String label
) {
}
