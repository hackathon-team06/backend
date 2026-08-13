package com.likelion.staycare.domain.weather.service;


import com.likelion.staycare.domain.weather.dto.OpenWeatherResponse;
import com.likelion.staycare.domain.weather.dto.WeatherMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherMessageService {

    private final WeatherClient weatherClient;

    public WeatherMessageResponse getWeatherMessage() {
        OpenWeatherResponse response = weatherClient.getCurrentWeather();

        String main = extractMain(response);

        return switch (main) {
            case "Clear" -> WeatherMessageResponse.builder()
                    .condition("CLEAR")
                    .messageTitle("오늘 햇빛이 쨍쨍해요")
                    .messageBody("선크림은 꼭 바르고 나가세요!")
                    .build();

            case "Clouds" -> WeatherMessageResponse.builder()
                    .condition("CLOUDS")
                    .messageTitle("오늘은 구름이 많아요")
                    .messageBody("가벼운 보습 케어를 챙겨보세요!")
                    .build();

            case "Rain", "Drizzle", "Thunderstorm" -> WeatherMessageResponse.builder()
                    .condition("RAIN")
                    .messageTitle("오늘은 비 소식이 있어요")
                    .messageBody("우산 챙기고, 습한 날씨엔 피부 진정을 신경 써보세요!")
                    .build();

            case "Snow" -> WeatherMessageResponse.builder()
                    .condition("SNOW")
                    .messageTitle("오늘은 눈이 와요")
                    .messageBody("보온에 신경 쓰고, 건조해지지 않게 보습도 챙겨주세요!")
                    .build();

            case "Mist", "Smoke", "Haze", "Dust", "Fog", "Sand", "Ash", "Squall", "Tornado" -> WeatherMessageResponse.builder()
                    .condition("HAZE")
                    .messageTitle("오늘은 공기가 탁할 수 있어요")
                    .messageBody("외출 후에는 세안과 진정을 꼼꼼히 해주세요!")
                    .build();

            default -> WeatherMessageResponse.builder()
                    .condition("UNKNOWN")
                    .messageTitle("오늘 날씨를 확인해보세요")
                    .messageBody("외출 전 피부 상태에 맞는 케어를 챙겨보세요!")
                    .build();
        };
    }

    private String extractMain(OpenWeatherResponse response) {
        if (response == null || response.weather() == null || response.weather().isEmpty()) {
            return "UNKNOWN";
        }
        return response.weather().get(0).main();
    }
}
