package com.likelion.staycare.domain.mission.dto.openai;

import java.util.List;
import java.util.Map;

public record OpenAiResponsesRequest(
        String model,
        List<InputMessage> input,
        TextConfig text
) {

    public record InputMessage(
            String role,
            List<InputContent> content
    ) {
    }

    public record InputContent(
            String type,
            String text
    ) {
    }

    public record TextConfig(
            FormatConfig format
    ) {
    }

    public record FormatConfig(
            String type,
            String name,
            String description,
            Map<String, Object> schema,
            boolean strict
    ) {
    }
}