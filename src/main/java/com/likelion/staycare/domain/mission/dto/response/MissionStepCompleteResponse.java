package com.likelion.staycare.domain.mission.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "미션 step 완료 처리 응답")
public record MissionStepCompleteResponse(
        @Schema(description = "이번 요청으로 step 완료 보상으로 적립된 포인트", example = "1")
        int stepRewardPoint,

        @Schema(description = "이번 요청으로 하루 전체 미션 완료 보너스로 적립된 포인트", example = "2")
        int dailyBonusPoint,

        @Schema(description = "이번 요청으로 실제 적립된 총 포인트", example = "3")
        int awardedPoint,

        @Schema(description = "현재 사용자의 누적 총 포인트", example = "17")
        int totalPoint,

        @Schema(description = "이번 완료로 현재 미션이 완료 상태가 되었는지 여부", example = "true")
        boolean missionCompleted,

        @Schema(description = "이번 완료로 오늘 아침/저녁 미션이 모두 완료되었는지 여부", example = "true")
        boolean dailyMissionsCompleted
) {
}
