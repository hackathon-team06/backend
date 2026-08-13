package com.likelion.staycare.domain.googlecalendar.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;

    private String authUri = "https://accounts.google.com/o/oauth2/v2/auth";
    private String tokenUri = "https://oauth2.googleapis.com/token";

    /**
     * 끌어오기만 할 거면 calendar.readonly 권장
     * 나중에 일정 생성/수정까지 할 거면 calendar 유지
     */
    private String scope = "https://www.googleapis.com/auth/calendar.readonly";
}
