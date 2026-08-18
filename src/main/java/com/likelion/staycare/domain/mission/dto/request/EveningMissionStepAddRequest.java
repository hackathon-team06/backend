package com.likelion.staycare.domain.mission.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(
        description = """
                저녁 미션 추가 요청입니다.
                AI 재추천 결과를 선택하거나 사용자가 직접 입력한 저녁 미션 문구를 부족한 슬롯 수만큼 추가합니다.
                """
)
public record EveningMissionStepAddRequest(
        @NotEmpty
        @Size(max = 3)
        @ArraySchema(
                schema = @Schema(
                        description = "추가할 저녁 미션 문구",
                        example = "세안 후 자극 없는 보습제를 충분히 바르기"
                ),
                arraySchema = @Schema(
                        description = "현재 비어 있는 저녁 미션 슬롯 수 이하로 보냅니다.",
                        example = "[\"세안 후 자극 없는 보습제를 충분히 바르기\"]"
                )
        )
        List<@NotBlank String> steps
) {
}
