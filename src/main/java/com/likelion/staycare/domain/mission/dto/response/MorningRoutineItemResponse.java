package com.likelion.staycare.domain.mission.dto.response;

import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "아침 루틴 항목 응답")
public record MorningRoutineItemResponse(
        Long itemId,
        String content,
        String category,
        MorningRoutineItemSource source,
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
