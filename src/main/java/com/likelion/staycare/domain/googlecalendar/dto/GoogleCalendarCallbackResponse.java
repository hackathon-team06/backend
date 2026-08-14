package com.likelion.staycare.domain.googlecalendar.dto;

import lombok.Builder;

@Builder
public record GoogleCalendarCallbackResponse(
        boolean success,
        String message,
        String code
) {
}
