package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.config.GoogleCalendarProperties;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventDateTime;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventItem;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventsListResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventsResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleOAuthTokenResponse;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarConnection;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarConnectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarApiService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");

    private final GoogleCalendarConnectionRepository connectionRepository;
    private final GoogleCalendarProperties properties;

    private final RestClient restClient = RestClient.create();

    /**
     * 사용자 조회용:
     * Google Calendar의 특정 날짜 이벤트를 읽어 응답으로만 반환한다.
     * 앱 DB에는 저장/수정/삭제를 하지 않는다.
     */
    @Transactional(readOnly = true)
    public GoogleCalendarEventsResponse getEventsByDate(Long userId, LocalDate date) {
        GoogleCalendarConnection connection = getValidConnection(userId);

        log.info("Google Calendar events fetch start. userId={}, date={}", userId, date);

        try {
            GoogleCalendarEventsListResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("www.googleapis.com")
                            .path("/calendar/v3/calendars/primary/events")
                            .queryParam("singleEvents", true)
                            .queryParam("orderBy", "startTime")
                            .queryParam("timeMin", date.atStartOfDay(KOREA_ZONE).toOffsetDateTime().toString())
                            .queryParam("timeMax", date.plusDays(1).atStartOfDay(KOREA_ZONE).toOffsetDateTime().toString())
                            .build())
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(GoogleCalendarEventsListResponse.class);

            List<GoogleCalendarEventItem> items =
                    response == null || response.items() == null ? List.of() : response.items();

            List<GoogleCalendarEventResponse> events = items.stream()
                    .filter(item -> !isCancelled(item))
                    .map(this::toResponse)
                    .toList();

            return GoogleCalendarEventsResponse.builder()
                    .events(events)
                    .build();

        } catch (HttpStatusCodeException e) {
            log.error("Google Calendar API error status={}", e.getStatusCode());
            log.error("Google Calendar API error body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google Calendar API unexpected error. userId={}, date={}", userId, date, e);
            throw e;
        }
    }

    /**
     * primary calendar 접근 자체가 되는지 확인용
     */
    @Transactional(readOnly = true)
    public String testPrimaryCalendarAccess(Long userId) {
        GoogleCalendarConnection connection = getValidConnection(userId);

        try {
            return restClient.get()
                    .uri("https://www.googleapis.com/calendar/v3/calendars/primary")
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);
        } catch (HttpStatusCodeException e) {
            log.error("Primary calendar access error status={}", e.getStatusCode());
            log.error("Primary calendar access error body={}", e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    private boolean isCancelled(GoogleCalendarEventItem item) {
        return item != null
                && item.status() != null
                && "cancelled".equalsIgnoreCase(item.status());
    }

    private GoogleCalendarEventResponse toResponse(GoogleCalendarEventItem item) {
        return GoogleCalendarEventResponse.builder()
                .googleEventId(item.id())
                .summary(item.summary())
                .description(item.description())
                .start(extractDateTime(item.start()))
                .end(extractDateTime(item.end()))
                .htmlLink(item.htmlLink())
                .build();
    }

    private String extractDateTime(GoogleCalendarEventDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        if (dateTime.dateTime() != null && !dateTime.dateTime().isBlank()) {
            return dateTime.dateTime();
        }
        return dateTime.date();
    }

    private GoogleCalendarConnection getValidConnection(Long userId) {
        GoogleCalendarConnection connection = connectionRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("구글 캘린더 연동 정보가 없습니다. 먼저 연동을 완료하세요."));

        if (connection.getTokenExpiresAt() == null ||
                connection.getTokenExpiresAt().isBefore(LocalDateTime.now().plusMinutes(1))) {
            refreshAccessToken(connection);
        }

        return connection;
    }

    private void refreshAccessToken(GoogleCalendarConnection connection) {
        if (connection.getRefreshToken() == null || connection.getRefreshToken().isBlank()) {
            throw new IllegalStateException("refresh token 이 없습니다. 다시 구글 연동을 진행하세요.");
        }

        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", properties.getClientId());
        formData.add("client_secret", properties.getClientSecret());
        formData.add("refresh_token", connection.getRefreshToken());
        formData.add("grant_type", "refresh_token");

        try {
            GoogleOAuthTokenResponse tokenResponse = restClient.post()
                    .uri(properties.getTokenUri())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(formData)
                    .retrieve()
                    .body(GoogleOAuthTokenResponse.class);

            if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                throw new IllegalStateException("access token 갱신에 실패했습니다.");
            }

            LocalDateTime expiresAt = LocalDateTime.now()
                    .plusSeconds(tokenResponse.expiresIn() == null ? 3600L : tokenResponse.expiresIn());

            String nextScope = (tokenResponse.scope() == null || tokenResponse.scope().isBlank())
                    ? connection.getScope()
                    : tokenResponse.scope();

            String nextTokenType = (tokenResponse.tokenType() == null || tokenResponse.tokenType().isBlank())
                    ? connection.getTokenType()
                    : tokenResponse.tokenType();

            connection.updateTokens(
                    tokenResponse.accessToken(),
                    connection.getRefreshToken(),
                    nextTokenType,
                    nextScope,
                    expiresAt
            );

            connectionRepository.save(connection);

            log.info("Google access token refreshed. connectionId={}, expiresAt={}",
                    connection.getId(), expiresAt);

        } catch (HttpStatusCodeException e) {
            log.error("Google token refresh status={}", e.getStatusCode());
            log.error("Google token refresh body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google token refresh unexpected error. connectionId={}", connection.getId(), e);
            throw e;
        }
    }
}
