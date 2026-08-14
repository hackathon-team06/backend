package com.likelion.staycare.domain.googlecalendar.dto;

import lombok.Builder;

@Builder
public record GoogleCalendarEventResponse(
        String googleEventId,
        String summary,
        String description,
        String start,
        String end,
        String htmlLink
) {
}
