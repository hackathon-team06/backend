    package com.likelion.staycare.domain.mission.dto.response;

    import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
    import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
    import io.swagger.v3.oas.annotations.media.Schema;
    import lombok.Builder;

    @Builder
    @Schema(description = "고정 아침 미션 1개 응답")
    public record MorningRoutineItemResponse(
            @Schema(description = "고정 아침 미션 항목 ID. 삭제 API의 itemId로 사용", example = "12")
            Long itemId,

            @Schema(description = "실제로 매일 수행할 미션 문구", example = "외출 전 자외선 차단제를 꼼꼼하게 바르기")
            String content,

            @Schema(description = "미션 category 한글 라벨. CUSTOM 항목은 null 가능", example = "자외선 차단", nullable = true)
            String category,

            @Schema(description = "미션 출처. 현재 고정 아침 미션 응답에서는 AI 또는 CUSTOM이 반환됩니다.", example = "AI")
            MorningRoutineItemSource source,

            @Schema(description = "현재 고정 루틴 안에서의 순서", example = "2")
            int itemOrder
    ) {
        public static MorningRoutineItemResponse from(MorningRoutineItem item) {
            return MorningRoutineItemResponse.builder()
                    .itemId(item.getId())
                    .content(item.getContent())
                    .category(item.getCategory())
                    .source(item.getSource())
                    .itemOrder(item.getItemOrder())
                    .build();
        }
    }
