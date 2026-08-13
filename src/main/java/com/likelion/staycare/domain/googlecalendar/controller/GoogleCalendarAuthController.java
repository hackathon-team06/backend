package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.config.GoogleCalendarProperties;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleCalendarOAuthService {

    private final GoogleCalendarProperties properties;

    public String createAuthorizationUrl(Long userId) {
        String rawState = userId + ":" + UUID.randomUUID();
        String encodedState = Base64.getUrlEncoder()
                .encodeToString(rawState.getBytes(StandardCharsets.UTF_8));

        return UriComponentsBuilder
                .fromUriString(properties.getAuthUri())
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", properties.getScope())
                .queryParam("access_type", "offline")
                .queryParam("include_granted_scopes", "true")
                .queryParam("prompt", "consent")
                .queryParam("state", encodedState)
                .build()
                .toUriString();
    }

    public GoogleCalendarCallbackResponse handleCallback(String code, String state, String error) {
        if (error != null && !error.isBlank()) {
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("구글 연동 실패: " + error)
                    .code(null)
                    .build();
        }

        if (code == null || code.isBlank()) {
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("authorization code가 없습니다.")
                    .code(null)
                    .build();
        }

        if (state == null || state.isBlank()) {
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("state가 없습니다.")
                    .code(null)
                    .build();
        }

        return GoogleCalendarCallbackResponse.builder()
                .success(true)
                .message("callback 진입 성공. 다음 단계에서 code로 access token / refresh token 교환을 진행하면 됩니다.")
                .code(code)
                .build();
    }
}
