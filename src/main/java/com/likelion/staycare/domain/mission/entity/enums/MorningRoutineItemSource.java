package com.likelion.staycare.domain.mission.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                고정 아침 미션 저장 시 사용하는 출처 코드입니다.
                AI=AI 추천 결과에서 사용자가 선택한 미션
                CUSTOM=사용자가 직접 작성한 미션

                SURVEY는 과거 구조 호환용 값이며, 현재 고정 아침 미션 저장 요청에는 사용하지 않습니다.
                """
)
public enum MorningRoutineItemSource {
    SURVEY,
    AI,
    CUSTOM
}
