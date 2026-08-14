package com.likelion.staycare.domain.googlecalendar.service;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventDateTime;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventItem;
import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarSyncResult;
import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarScheduleLink;
import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarScheduleLinkRepository;
import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import com.likelion.staycare.domain.schedule.repository.ScheduleRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleCalendarScheduleSyncService {

    private static final String PRIMARY_CALENDAR_ID = "primary";

    private final GoogleCalendarApiService googleCalendarApiService;
    private final GoogleCalendarScheduleLinkRepository linkRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GoogleCalendarSyncResult syncDate(Long userId, LocalDate date) {
        log.info("Google Calendar sync start. userId={}, date={}", userId, date);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<GoogleCalendarEventItem> events = googleCalendarApiService.getRawEventsByDate(userId, date);

        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (GoogleCalendarEventItem event : events) {
            try {
                if (event.id() == null || event.id().isBlank()) {
                    skipped++;
                    continue;
                }

                Optional<GoogleCalendarScheduleLink> optionalLink =
                        linkRepository.findByUser_IdAndGoogleEventId(userId, event.id());

                log.info("Google Calendar sync event. eventId={}, summary={}, status={}, linked={}",
                        event.id(), event.summary(), event.status(), optionalLink.isPresent());

                // 구글에서 삭제/취소된 일정
                if ("cancelled".equalsIgnoreCase(event.status())) {
                    if (optionalLink.isPresent()) {
                        GoogleCalendarScheduleLink link = optionalLink.get();
                        Schedule schedule = link.getSchedule();

                        // FK 때문에 링크 먼저 삭제
                        linkRepository.delete(link);

                        if (schedule != null) {
                            scheduleRepository.delete(schedule);
                        }

                        updated++;
                        log.info("Google Calendar cancelled event removed from local DB. googleEventId={}", event.id());
                    } else {
                        skipped++;
                    }
                    continue;
                }


                ParsedEvent parsed = parseEvent(event);

                if (optionalLink.isPresent()) {
                    GoogleCalendarScheduleLink link = optionalLink.get();
                    Schedule schedule = link.getSchedule();

                    if (schedule == null) {
                        skipped++;
                        continue;
                    }

                    schedule.updateSchedule(
                            normalizeTitle(event.summary()),
                            parsed.startDate(),
                            parsed.endDate(),
                            parsed.startTime(),
                            parsed.endTime(),
                            schedule.getCompanion() != null ? schedule.getCompanion() : defaultCompanion(),
                            schedule.getCategory() != null ? schedule.getCategory() : defaultCategory()
                    );

                    scheduleRepository.save(schedule);
                    link.touchSyncedAt();
                    linkRepository.save(link);
                    updated++;

                    log.info("Google Calendar sync updated local schedule. scheduleId={}, googleEventId={}",
                            schedule.getId(), event.id());

                } else {
                    Schedule schedule = Schedule.builder()
                            .user(user)
                            .title(normalizeTitle(event.summary()))
                            .startDate(parsed.startDate())
                            .endDate(parsed.endDate())
                            .startTime(parsed.startTime())
                            .endTime(parsed.endTime())
                            .companion(defaultCompanion())
                            .category(defaultCategory())
                            .build();

                    scheduleRepository.save(schedule);

                    GoogleCalendarScheduleLink link = GoogleCalendarScheduleLink.builder()
                            .user(user)
                            .schedule(schedule)
                            .googleCalendarId(PRIMARY_CALENDAR_ID)
                            .googleEventId(event.id())
                            .lastSyncedAt(LocalDateTime.now())
                            .build();

                    linkRepository.save(link);
                    created++;

                    log.info("Google Calendar sync created local schedule. scheduleId={}, googleEventId={}",
                            schedule.getId(), event.id());
                }

            } catch (Exception e) {
                log.error("Google Calendar single event sync failed. userId={}, date={}, eventId={}",
                        userId, date, event.id(), e);
                skipped++;
            }
        }

        log.info("Google Calendar sync finished. userId={}, date={}, fetched={}, created={}, updated={}, skipped={}",
                userId, date, events.size(), created, updated, skipped);

        return GoogleCalendarSyncResult.builder()
                .totalFetched(events.size())
                .createdCount(created)
                .updatedCount(updated)
                .skippedCount(skipped)
                .build();
    }

    private String normalizeTitle(String summary) {
        String title = (summary == null || summary.isBlank()) ? "제목 없음" : summary.trim();
        return title.length() > 100 ? title.substring(0, 100) : title;
    }

    private ParsedEvent parseEvent(GoogleCalendarEventItem event) {
        GoogleCalendarEventDateTime start = event.start();
        GoogleCalendarEventDateTime end = event.end();

        if (start == null) {
            throw new IllegalArgumentException("Google 이벤트 시작 시간이 없습니다. eventId=" + event.id());
        }

        // 종일 일정: Google end.date는 exclusive
        if (start.date() != null && !start.date().isBlank()) {
            LocalDate startDate = LocalDate.parse(start.date());

            LocalDate endDate = startDate;
            if (end != null && end.date() != null && !end.date().isBlank()) {
                LocalDate exclusiveEndDate = LocalDate.parse(end.date());
                LocalDate inclusiveEndDate = exclusiveEndDate.minusDays(1);
                endDate = inclusiveEndDate.isBefore(startDate) ? startDate : inclusiveEndDate;
            }

            return new ParsedEvent(startDate, endDate, null, null);
        }

        // 시간 일정
        if (start.dateTime() != null && !start.dateTime().isBlank()) {
            OffsetDateTime startDateTime = OffsetDateTime.parse(start.dateTime());
            LocalDate startDate = startDateTime.toLocalDate();
            LocalTime startTime = startDateTime.toLocalTime().withNano(0);

            LocalDate endDate = startDate;
            LocalTime endTime = null;

            if (end != null && end.dateTime() != null && !end.dateTime().isBlank()) {
                OffsetDateTime endDateTime = OffsetDateTime.parse(end.dateTime());
                endDate = endDateTime.toLocalDate();
                endTime = endDateTime.toLocalTime().withNano(0);
            }

            return new ParsedEvent(startDate, endDate, startTime, endTime);
        }

        throw new IllegalArgumentException("Google 이벤트 시간 형식을 해석할 수 없습니다. eventId=" + event.id());
    }

    private Companion defaultCompanion() {
        return Companion.ALONE;
    }

    private ScheduleCategory defaultCategory() {
        return ScheduleCategory.EVENT;
    }

    private record ParsedEvent(
            LocalDate startDate,
            LocalDate endDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
    }
}
