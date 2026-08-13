package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.config.GoogleCalendarProperties;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleOAuthTokenResponse;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarConnection;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarConnectionRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleCalendarOAuthService {

    private final GoogleCalendarProperties properties;
    private final GoogleCalendarConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    private final RestClient restClient = RestClient.create();

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

    @Transactional
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

        Long userId = extractUserIdFromState(state);
        GoogleOAuthTokenResponse tokenResponse = exchangeCodeForToken(code);
        saveOrUpdateConnection(userId, tokenResponse);

        return GoogleCalendarCallbackResponse.builder()
                .success(true)
                .message("구글 캘린더 연동이 완료되었습니다.")
                .code(null)
                .build();

    }

    private Long extractUserIdFromState(String state) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(state), StandardCharsets.UTF_8);
            String userIdString = decoded.split(":")[0];
            return Long.valueOf(userIdString);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 state 입니다.");
        }
    }

    private GoogleOAuthTokenResponse exchangeCodeForToken(String code) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", properties.getClientId());
        formData.add("client_secret", properties.getClientSecret());
        formData.add("redirect_uri", properties.getRedirectUri());
        formData.add("grant_type", "authorization_code");

        GoogleOAuthTokenResponse response = restClient.post()
                .uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(GoogleOAuthTokenResponse.class);

        if (response == null || response.accessToken() == null || response.accessToken().isBlank()) {
            throw new IllegalStateException("Google token 응답이 비어 있습니다.");
        }

        return response;
    }

    private void saveOrUpdateConnection(Long userId, GoogleOAuthTokenResponse tokenResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(tokenResponse.expiresIn() == null ? 3600L : tokenResponse.expiresIn());

        GoogleCalendarConnection connection = connectionRepository.findByUser_Id(userId)
                .orElseGet(() -> GoogleCalendarConnection.builder()
                        .user(user)
                        .accessToken(tokenResponse.accessToken())
                        .refreshToken(tokenResponse.refreshToken())
                        .tokenType(tokenResponse.tokenType())
                        .scope(tokenResponse.scope())
                        .tokenExpiresAt(expiresAt)
                        .connected(true)
                        .build()
                );

        if (connection.getId() != null) {
            connection.updateTokens(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.tokenType(),
                    tokenResponse.scope(),
                    expiresAt
            );
        }

        connectionRepository.save(connection);
    }
}
