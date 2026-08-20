package com.likelion.staycare.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "weather.openweather")
public record OpenWeatherProperties(
        String apiKey,
        String baseUrl,
        double lat,
        double lon
) {
}
