package com.likelion.staycare.domain.mission.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = """
                고정 아침 미션 저장 시 사용하는 출처 코드입니다.
                AI = AI 추천 결과를 선택한 미션
                SURVEY = 생활 루틴 후보 7개 중 사용자가 선택한 미션
                CUSTOM = 사용자가 직접 입력한 미션

                SURVEY 이름은 기존 DB 호환을 위해 유지합니다.
                """
)
public enum MorningRoutineItemSource {
    SURVEY,
    AI,
    CUSTOM
}
