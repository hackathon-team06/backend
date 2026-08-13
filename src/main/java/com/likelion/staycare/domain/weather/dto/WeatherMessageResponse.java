package com.likelion.staycare.domain.weather.dto;

import lombok.Builder;

@Builder
public record WeatherMessageResponse(
        String condition,
        String messageTitle,
        String messageBody
) {
}
