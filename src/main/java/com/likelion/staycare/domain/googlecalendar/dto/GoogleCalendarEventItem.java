package com.likelion.staycare.domain.googlecalendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleCalendarEventItem(
        @JsonProperty("id")
        String id,

        @JsonProperty("summary")
        String summary,

        @JsonProperty("description")
        String description,

        @JsonProperty("htmlLink")
        String htmlLink,

        @JsonProperty("status")
        String status,

        @JsonProperty("start")
        GoogleCalendarEventDateTime start,

        @JsonProperty("end")
        GoogleCalendarEventDateTime end
) {
}
