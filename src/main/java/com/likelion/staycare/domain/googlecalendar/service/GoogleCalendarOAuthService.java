package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.config.GoogleCalendarProperties;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleOAuthTokenResponse;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarConnection;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarConnectionRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarOAuthService {

    private final GoogleCalendarProperties properties;
    private final GoogleCalendarConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    private final RestClient restClient = RestClient.create();

    public String createAuthorizationUrl(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        String rawState = userId + ":" + UUID.randomUUID();
        String encodedState = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(rawState.getBytes(StandardCharsets.UTF_8));

        String authorizationUrl = UriComponentsBuilder
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

        log.info("Google OAuth authorization URL created. userId={}, redirectUri={}",
                userId, properties.getRedirectUri());

        return authorizationUrl;
    }

    @Transactional
    public GoogleCalendarCallbackResponse handleCallback(String code, String state, String error) {
        log.info("Google callback received. codePresent={}, statePresent={}, error={}",
                hasText(code), hasText(state), error);

        if (hasText(error)) {
            log.warn("Google OAuth returned error. error={}", error);
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("구글 연동 실패: " + error)
                    .code(null)
                    .build();
        }

        if (!hasText(code)) {
            log.warn("Google callback failed: authorization code missing.");
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("authorization code가 없습니다.")
                    .code(null)
                    .build();
        }

        if (!hasText(state)) {
            log.warn("Google callback failed: state missing.");
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("state가 없습니다.")
                    .code(null)
                    .build();
        }

        try {
            Long userId = extractUserIdFromState(state);
            GoogleOAuthTokenResponse tokenResponse = exchangeCodeForToken(code);
            saveOrUpdateConnection(userId, tokenResponse);

            log.info("Google Calendar connection completed. userId={}, scope={}",
                    userId, tokenResponse.scope());

            return GoogleCalendarCallbackResponse.builder()
                    .success(true)
                    .message("구글 캘린더 연동이 완료되었습니다.")
                    .code(null)
                    .build();

        } catch (IllegalArgumentException e) {
            log.error("Google callback validation failed. state={}", state, e);
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .code("G001")
                    .build();

        } catch (IllegalStateException e) {
            log.error("Google callback processing failed. state={}", state, e);
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .code("G002")
                    .build();

        } catch (Exception e) {
            log.error("Unexpected Google callback error. state={}", state, e);
            return GoogleCalendarCallbackResponse.builder()
                    .success(false)
                    .message("구글 연동 처리 중 서버 오류가 발생했습니다.")
                    .code("G003")
                    .build();
        }
    }

    private Long extractUserIdFromState(String state) {
        try {
            String decoded = new String(
                    Base64.getUrlDecoder().decode(state),
                    StandardCharsets.UTF_8
            );

            String[] parts = decoded.split(":", 2);
            if (parts.length < 2 || !hasText(parts[0])) {
                throw new IllegalArgumentException("유효하지 않은 state 입니다.");
            }

            return Long.valueOf(parts[0]);

        } catch (IllegalArgumentException e) {
            throw e;
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

        try {
            log.info("Google token exchange start. redirectUri={}", properties.getRedirectUri());

            GoogleOAuthTokenResponse response = restClient.post()
                    .uri(properties.getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(GoogleOAuthTokenResponse.class);

            if (response == null || !hasText(response.accessToken())) {
                throw new IllegalStateException("Google token 응답이 비어 있습니다.");
            }

            log.info("Google token exchange success. scope={}, expiresIn={}",
                    response.scope(), response.expiresIn());

            return response;

        } catch (RestClientResponseException e) {
            log.error("Google token exchange failed. status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);

            throw new IllegalStateException("Google token 교환에 실패했습니다.");

        } catch (Exception e) {
            log.error("Google token exchange unexpected error. redirectUri={}",
                    properties.getRedirectUri(), e);

            throw new IllegalStateException("Google token 교환 중 오류가 발생했습니다.");
        }
    }

    private void saveOrUpdateConnection(Long userId, GoogleOAuthTokenResponse tokenResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(tokenResponse.expiresIn() == null ? 3600L : tokenResponse.expiresIn());

        GoogleCalendarConnection existing = connectionRepository.findByUser_Id(userId).orElse(null);

        if (existing == null) {
            GoogleCalendarConnection newConnection = GoogleCalendarConnection.builder()
                    .user(user)
                    .accessToken(tokenResponse.accessToken())
                    .refreshToken(tokenResponse.refreshToken())
                    .tokenType(defaultIfBlank(tokenResponse.tokenType(), "Bearer"))
                    .scope(tokenResponse.scope())
                    .tokenExpiresAt(expiresAt)
                    .connected(true)
                    .build();

            connectionRepository.save(newConnection);
            log.info("Google Calendar connection created. userId={}", userId);
            return;
        }

        String nextRefreshToken = hasText(tokenResponse.refreshToken())
                ? tokenResponse.refreshToken()
                : existing.getRefreshToken();

        String nextTokenType = hasText(tokenResponse.tokenType())
                ? tokenResponse.tokenType()
                : existing.getTokenType();

        String nextScope = hasText(tokenResponse.scope())
                ? tokenResponse.scope()
                : existing.getScope();

        existing.updateTokens(
                tokenResponse.accessToken(),
                nextRefreshToken,
                nextTokenType,
                nextScope,
                expiresAt
        );

        /*
         * GoogleCalendarConnection 엔티티에 setConnected(true) 또는 reconnect() 같은 메서드가 있으면
         * 여기서 함께 호출하세요.
         *
         * 예:
         * existing.setConnected(true);
         */

        connectionRepository.save(existing);
        log.info("Google Calendar connection updated. userId={}, connectionId={}",
                userId, existing.getId());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return hasText(value) ? value : defaultValue;
    }
}
