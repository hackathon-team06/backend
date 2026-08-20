package com.likelion.staycare.domain.mission.dto.openai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenAiResponsesResponse(
        String id,
        String status,
        @JsonProperty("output_text")
        String outputText,
        List<OutputItem> output,
        ApiError error
) {

    public record OutputItem(
            String type,
            List<OutputContent> content
    ) {
    }

    public record OutputContent(
            String type,
            String text
    ) {
    }

    public record ApiError(
            String message,
            String type,
            String param,
            String code
    ) {
    }
}
