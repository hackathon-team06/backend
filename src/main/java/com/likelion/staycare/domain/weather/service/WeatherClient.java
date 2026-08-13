package com.likelion.staycare.domain.weather.service;


import com.likelion.staycare.domain.weather.dto.OpenWeatherResponse;
import com.likelion.staycare.global.config.OpenWeatherProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class WeatherClient {

    private final OpenWeatherProperties properties;
    private final RestClient restClient = RestClient.create();

    public OpenWeatherResponse getCurrentWeather() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.openweathermap.org")
                        .path("/data/2.5/weather")
                        .queryParam("lat", properties.lat())
                        .queryParam("lon", properties.lon())
                        .queryParam("appid", properties.apiKey())
                        .queryParam("lang", "kr")
                        .queryParam("units", "metric")
                        .build())
                .retrieve()
                .body(OpenWeatherResponse.class);
    }
}
