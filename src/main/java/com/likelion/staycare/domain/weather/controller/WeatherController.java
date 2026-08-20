package com.likelion.staycare.domain.weather.controller;


import com.likelion.staycare.domain.weather.dto.WeatherMessageResponse;
import com.likelion.staycare.domain.weather.service.WeatherMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

@Tag(name = "Weather", description = "날씨 API(토큰 필요)")

@RequestMapping("/api/")
public class WeatherController {

    private final WeatherMessageService weatherMessageService;

    @Operation(summary = "날씨 api 조회", description = "현재 날씨 및 메세지를 조회합니다.")
    @GetMapping("/weather-message")
    public WeatherMessageResponse getWeatherMessage() {
        return weatherMessageService.getWeatherMessage();
    }
}
