package com.likelion.staycare.domain.googlecalendar.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleCalendarCreateEventResponse(
        @JsonProperty("id")
        String id,

        @JsonProperty("htmlLink")
        String htmlLink
) {
}
