package com.likelion.staycare.domain.googlecalendar.dto;

import lombok.Builder;

@Builder
public record GoogleCalendarSyncResult(
        int totalFetched,
        int createdCount,
        int updatedCount,
        int skippedCount
) {
}
