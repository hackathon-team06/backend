package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.config.GoogleCalendarProperties;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCreateEventResponse;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleOAuthTokenResponse;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarConnection;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarScheduleLink;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarConnectionRepository;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarScheduleLinkRepository;
import com.likelion.staycare.domain.schedule.entity.Schedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GoogleCalendarSchedulePushService {

    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter RFC3339_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private final GoogleCalendarConnectionRepository connectionRepository;
    private final GoogleCalendarScheduleLinkRepository linkRepository;
    private final GoogleCalendarProperties properties;

    private final RestClient restClient = RestClient.create();

    public void createGoogleEventIfConnected(Schedule schedule) {
        Long userId = schedule.getUser().getId();

        log.info("Google push start. scheduleId={}, userId={}", schedule.getId(), userId);

        Optional<GoogleCalendarConnection> connectionOpt = connectionRepository.findByUser_Id(userId);

        if (connectionOpt.isEmpty()) {
            log.warn("Google Calendar not connected. skip push. userId={}", userId);
            return;
        }

        if (linkRepository.findBySchedule_Id(schedule.getId()).isPresent()) {
            log.warn("Google link already exists. skip duplicate push. scheduleId={}", schedule.getId());
            return;
        }

        GoogleCalendarConnection connection = getValidConnection(connectionOpt.get());

        log.info("Google push scope={}", connection.getScope());

        if (connection.getScope() == null ||
                !connection.getScope().contains("https://www.googleapis.com/auth/calendar")) {
            throw new IllegalStateException("Google Calendar 쓰기 권한(scope)이 없습니다. 다시 연동하세요.");
        }

        Map<String, Object> requestBody = buildEventRequest(schedule);
        log.info("Google push requestBody={}", requestBody);

        try {
            GoogleCalendarCreateEventResponse response = restClient.post()
                    .uri("https://www.googleapis.com/calendar/v3/calendars/primary/events")
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GoogleCalendarCreateEventResponse.class);

            log.info("Google push response={}", response);

            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new IllegalStateException("Google Calendar event 생성 응답이 비어 있습니다.");
            }

            GoogleCalendarScheduleLink link = GoogleCalendarScheduleLink.builder()
                    .user(schedule.getUser())
                    .schedule(schedule)
                    .googleCalendarId("primary")
                    .googleEventId(response.id())
                    .lastSyncedAt(LocalDateTime.now())
                    .build();

            linkRepository.save(link);

            log.info("Google Calendar event created. scheduleId={}, googleEventId={}",
                    schedule.getId(), response.id());

        } catch (HttpStatusCodeException e) {
            log.error("Google Calendar create event status={}", e.getStatusCode());
            log.error("Google Calendar create event body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google Calendar create event unexpected error. scheduleId={}", schedule.getId(), e);
            throw e;
        }
    }

    public void updateGoogleEventIfLinked(Schedule schedule) {
        Long userId = schedule.getUser().getId();

        Optional<GoogleCalendarScheduleLink> linkOpt = linkRepository.findBySchedule_Id(schedule.getId());
        if (linkOpt.isEmpty()) {
            log.warn("Google link not found. skip update. scheduleId={}", schedule.getId());
            return;
        }

        Optional<GoogleCalendarConnection> connectionOpt = connectionRepository.findByUser_Id(userId);
        if (connectionOpt.isEmpty()) {
            log.warn("Google Calendar not connected. skip update. userId={}", userId);
            return;
        }

        GoogleCalendarConnection connection = getValidConnection(connectionOpt.get());
        GoogleCalendarScheduleLink link = linkOpt.get();

        Map<String, Object> requestBody = buildEventRequest(schedule);
        log.info("Google update requestBody={}", requestBody);

        try {
            GoogleCalendarCreateEventResponse response = restClient.patch()
                    .uri("https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events/{eventId}",
                            link.getGoogleCalendarId(), link.getGoogleEventId())
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GoogleCalendarCreateEventResponse.class);

            link.touchSyncedAt();

            log.info("Google Calendar event updated. scheduleId={}, googleEventId={}, response={}",
                    schedule.getId(), link.getGoogleEventId(), response);

        } catch (HttpStatusCodeException e) {
            log.error("Google Calendar update event status={}", e.getStatusCode());
            log.error("Google Calendar update event body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google Calendar update event unexpected error. scheduleId={}", schedule.getId(), e);
            throw e;
        }
    }

    public void cancelGoogleEventIfLinked(Schedule schedule) {
        Long userId = schedule.getUser().getId();

        Optional<GoogleCalendarScheduleLink> linkOpt = linkRepository.findBySchedule_Id(schedule.getId());
        if (linkOpt.isEmpty()) {
            log.warn("Google link not found. skip cancel. scheduleId={}", schedule.getId());
            return;
        }

        Optional<GoogleCalendarConnection> connectionOpt = connectionRepository.findByUser_Id(userId);
        if (connectionOpt.isEmpty()) {
            log.warn("Google Calendar not connected. skip cancel. userId={}", userId);
            return;
        }

        GoogleCalendarConnection connection = getValidConnection(connectionOpt.get());
        GoogleCalendarScheduleLink link = linkOpt.get();

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("status", "cancelled");

        try {
            GoogleCalendarCreateEventResponse response = restClient.patch()
                    .uri("https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events/{eventId}",
                            link.getGoogleCalendarId(), link.getGoogleEventId())
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GoogleCalendarCreateEventResponse.class);

            link.touchSyncedAt();

            log.info("Google Calendar event cancelled. scheduleId={}, googleEventId={}, response={}",
                    schedule.getId(), link.getGoogleEventId(), response);

        } catch (HttpStatusCodeException e) {
            log.error("Google Calendar cancel event status={}", e.getStatusCode());
            log.error("Google Calendar cancel event body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google Calendar cancel event unexpected error. scheduleId={}", schedule.getId(), e);
            throw e;
        }
    }

    public void deleteGoogleEventIfLinked(Schedule schedule) {
        Long userId = schedule.getUser().getId();

        Optional<GoogleCalendarScheduleLink> linkOpt = linkRepository.findBySchedule_Id(schedule.getId());
        if (linkOpt.isEmpty()) {
            log.warn("Google link not found. skip delete. scheduleId={}", schedule.getId());
            return;
        }

        Optional<GoogleCalendarConnection> connectionOpt = connectionRepository.findByUser_Id(userId);
        if (connectionOpt.isEmpty()) {
            log.warn("Google Calendar not connected. skip delete. userId={}", userId);
            return;
        }

        GoogleCalendarConnection connection = getValidConnection(connectionOpt.get());
        GoogleCalendarScheduleLink link = linkOpt.get();

        try {
            restClient.delete()
                    .uri("https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events/{eventId}",
                            link.getGoogleCalendarId(), link.getGoogleEventId())
                    .headers(headers -> headers.setBearerAuth(connection.getAccessToken()))
                    .retrieve()
                    .toBodilessEntity();

            linkRepository.delete(link);

            log.info("Google Calendar event deleted. scheduleId={}, googleEventId={}",
                    schedule.getId(), link.getGoogleEventId());

        } catch (HttpStatusCodeException e) {
            log.error("Google Calendar delete event status={}", e.getStatusCode());
            log.error("Google Calendar delete event body={}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("Google Calendar delete event unexpected error. scheduleId={}", schedule.getId(), e);
            throw e;
        }
    }


    private Map<String, Object> buildEventRequest(Schedule schedule) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("summary", normalizeTitle(schedule.getTitle()));
        body.put("description", buildDescription(schedule));

        // 종일 일정
        if (schedule.getStartTime() == null && schedule.getEndTime() == null) {
            body.put("start", Map.of(
                    "date", schedule.getScheduleDate().toString()
            ));
            body.put("end", Map.of(
                    "date", schedule.getScheduleDate().plusDays(1).toString()
            ));

            body.put("extendedProperties", Map.of(
                    "private", buildPrivateExtendedProperties(schedule)
            ));
            return body;
        }

        ZonedDateTime startDateTime = schedule.getScheduleDate()
                .atTime(schedule.getStartTime())
                .atZone(KOREA_ZONE);

        ZonedDateTime endDateTime = schedule.getScheduleDate()
                .atTime(schedule.getEndTime() != null
                        ? schedule.getEndTime()
                        : schedule.getStartTime().plusHours(1))
                .atZone(KOREA_ZONE);

        body.put("start", Map.of(
                "dateTime", startDateTime.format(RFC3339_FORMATTER),
                "timeZone", "Asia/Seoul"
        ));
        body.put("end", Map.of(
                "dateTime", endDateTime.format(RFC3339_FORMATTER),
                "timeZone", "Asia/Seoul"
        ));

        // category / companion / appStatus 같은 앱 내부 메타데이터는 top-level이 아니라 extendedProperties에 저장
        body.put("extendedProperties", Map.of(
                "private", buildPrivateExtendedProperties(schedule)
        ));

        // Google event status는 ACTIVE가 아니라 confirmed/tentative/cancelled 만 허용
        body.put("status", "confirmed");

        return body;
    }

    private Map<String, String> buildPrivateExtendedProperties(Schedule schedule) {
        Map<String, String> privateProps = new LinkedHashMap<>();
        privateProps.put("scheduleId", String.valueOf(schedule.getId()));
        privateProps.put("scheduleCategory", schedule.getCategory() != null ? schedule.getCategory().name() : "");
        privateProps.put("companion", schedule.getCompanion() != null ? schedule.getCompanion().name() : "");
        privateProps.put("appStatus", schedule.getStatus() != null ? schedule.getStatus().name() : "");
        return privateProps;
    }

    private String buildDescription(Schedule schedule) {
        String category = schedule.getCategory() != null ? schedule.getCategory().name() : "-";
        String companion = schedule.getCompanion() != null ? schedule.getCompanion().name() : "-";
        String status = schedule.getStatus() != null ? schedule.getStatus().name() : "-";

        return "Stay-Care 앱에서 생성된 일정입니다.\n"
                + "category=" + category + "\n"
                + "companion=" + companion + "\n"
                + "status=" + status;
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Stay-Care 일정";
        }
        return title;
    }

    private GoogleCalendarConnection getValidConnection(GoogleCalendarConnection connection) {
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

        GoogleOAuthTokenResponse tokenResponse = restClient.post()
                .uri(properties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(GoogleOAuthTokenResponse.class);

        if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
            throw new IllegalStateException("Google access token 갱신에 실패했습니다.");
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
    }
}
