package com.likelion.staycare.domain.googlecalendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleCalendarEventDateTime(
        @JsonProperty("dateTime")
        String dateTime,

        @JsonProperty("date")
        String date
) {
}
