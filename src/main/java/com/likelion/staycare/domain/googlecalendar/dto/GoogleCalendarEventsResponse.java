package com.likelion.staycare.domain.googlecalendar.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record GoogleCalendarEventsResponse(
        List<GoogleCalendarEventResponse> events
) {
}
