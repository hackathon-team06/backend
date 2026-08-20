package com.likelion.staycare.domain.weather.dto;

import java.util.List;

public record OpenWeatherResponse(
        List<Weather> weather
) {
    public record Weather(
            Long id,
            String main,
            String description,
            String icon
    ) {
    }
}
