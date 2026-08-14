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
import java.util.ArrayList;
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
     * 사용자 응답용:
     * cancelled 이벤트는 제외하고 반환
     */
    @Transactional(readOnly = true)
    public GoogleCalendarEventsResponse getEventsByDate(Long userId, LocalDate date) {
        List<GoogleCalendarEventItem> rawEvents = getRawEventsByDate(userId, date);

        List<GoogleCalendarEventResponse> events = rawEvents.stream()
                .filter(item -> !isCancelled(item))
                .map(this::toResponse)
                .toList();

        return GoogleCalendarEventsResponse.builder()
                .events(events)
                .build();
    }

    /**
     * 동기화용:
     * cancelled 이벤트도 포함해서 반환
     *
     * 현재는 안정성을 위해 Google에 timeMin/timeMax를 직접 주지 않고,
     * primary calendar 이벤트를 읽은 뒤 서버에서 날짜 필터링한다.
     * 페이지네이션(nextPageToken)도 처리한다.
     */
    @Transactional(readOnly = true)
    public List<GoogleCalendarEventItem> getRawEventsByDate(Long userId, LocalDate date) {
        GoogleCalendarConnection connection = getValidConnection(userId);

        log.info("Google Calendar events fetch start. userId={}, date={}", userId, date);
        log.info("Google Calendar scope={}", connection.getScope());

        try {
            List<GoogleCalendarEventItem> allItems = new ArrayList<>();
            String pageToken = null;

            do {
                String currentPageToken = pageToken;

                GoogleCalendarEventsListResponse response = restClient.get()
                        .uri(uriBuilder -> {
                            var builder = uriBuilder
                                    .scheme("https")
                                    .host("www.googleapis.com")
                                    .path("/calendar/v3/calendars/primary/events")
                                    .queryParam("singleEvents", true)
                                    .queryParam("maxResults", 250)
                                    .queryParam("showDeleted", true);

                            if (currentPageToken != null && !currentPageToken.isBlank()) {
                                builder.queryParam("pageToken", currentPageToken);
                            }

                            return builder.build();
                        })
                        .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(GoogleCalendarEventsListResponse.class);

                List<GoogleCalendarEventItem> pageItems =
                        response == null || response.items() == null ? List.of() : response.items();

                allItems.addAll(pageItems);

                pageToken = response == null ? null : response.nextPageToken();

                log.info("Google Calendar page fetched. pageCount={}, nextPageTokenExists={}",
                        pageItems.size(), pageToken != null && !pageToken.isBlank());

            } while (pageToken != null && !pageToken.isBlank());

            log.info("Google Calendar raw events fetched. totalCount={}", allItems.size());

            for (GoogleCalendarEventItem item : allItems) {
                log.info("RAW eventId={}, summary={}, status={}, start={}, end={}",
                        item.id(),
                        item.summary(),
                        item.status(),
                        item.start(),
                        item.end());
            }

            List<GoogleCalendarEventItem> filtered = allItems.stream()
                    .filter(item -> isEventOnDate(item, date))
                    .toList();

            log.info("Google Calendar filtered events. count={}", filtered.size());

            for (GoogleCalendarEventItem item : filtered) {
                log.info("FILTERED eventId={}, summary={}, status={}, start={}, end={}",
                        item.id(),
                        item.summary(),
                        item.status(),
                        item.start(),
                        item.end());
            }

            return filtered;

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

    private boolean isEventOnDate(GoogleCalendarEventItem item, LocalDate targetDate) {
        GoogleCalendarEventDateTime start = item.start();
        GoogleCalendarEventDateTime end = item.end();

        if (start == null) {
            return false;
        }

        // 종일 일정: end.date 는 Google 규격상 exclusive
        if (start.date() != null && !start.date().isBlank()) {
            LocalDate startDate = LocalDate.parse(start.date());

            LocalDate endDateExclusive = (end != null && end.date() != null && !end.date().isBlank())
                    ? LocalDate.parse(end.date())
                    : startDate.plusDays(1);

            return !targetDate.isBefore(startDate) && targetDate.isBefore(endDateExclusive);
        }

        // 시간 지정 일정
        if (start.dateTime() != null && !start.dateTime().isBlank()) {
            OffsetDateTime startDateTime = OffsetDateTime.parse(start.dateTime());
            LocalDate eventStartDate = startDateTime.atZoneSameInstant(KOREA_ZONE).toLocalDate();

            LocalDate eventEndDate = eventStartDate;
            if (end != null && end.dateTime() != null && !end.dateTime().isBlank()) {
                OffsetDateTime endDateTime = OffsetDateTime.parse(end.dateTime());
                eventEndDate = endDateTime.atZoneSameInstant(KOREA_ZONE)
                        .minusNanos(1)
                        .toLocalDate();
            }

            return !targetDate.isBefore(eventStartDate) && !targetDate.isAfter(eventEndDate);
        }

        return false;
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
