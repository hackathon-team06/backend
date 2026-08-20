package com.likelion.staycare.domain.googlecalendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleCalendarEventsListResponse(
        @JsonProperty("items")
        List<GoogleCalendarEventItem> items,

        @JsonProperty("nextPageToken")
        String nextPageToken
) {
}
