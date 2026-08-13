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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleCalendarScheduleSyncService {

    private final GoogleCalendarApiService googleCalendarApiService;
    private final GoogleCalendarScheduleLinkRepository linkRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public GoogleCalendarSyncResult syncDate(Long userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<GoogleCalendarEventItem> events = googleCalendarApiService.getRawEventsByDate(userId, date);

        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (GoogleCalendarEventItem event : events) {
            if (event.id() == null || event.id().isBlank()) {
                skipped++;
                continue;
            }

            ParsedEvent parsed = parseEvent(event);

            Optional<GoogleCalendarScheduleLink> optionalLink =
                    linkRepository.findByUser_IdAndGoogleEventId(userId, event.id());

            if (optionalLink.isPresent()) {
                GoogleCalendarScheduleLink link = optionalLink.get();
                Schedule schedule = link.getSchedule();

                schedule.updateFromGoogle(
                        normalizeTitle(event.summary()),
                        parsed.scheduleDate(),
                        parsed.startTime(),
                        parsed.endTime(),
                        defaultCompanion(),
                        defaultCategory()
                );

                link.touchSyncedAt();
                updated++;
            } else {
                Schedule schedule = Schedule.builder()
                        .user(user)
                        .title(normalizeTitle(event.summary()))
                        .scheduleDate(parsed.scheduleDate())
                        .startTime(parsed.startTime())
                        .endTime(parsed.endTime())
                        .companion(defaultCompanion())
                        .category(defaultCategory())
                        .build();

                scheduleRepository.save(schedule);

                GoogleCalendarScheduleLink link = GoogleCalendarScheduleLink.builder()
                        .user(user)
                        .schedule(schedule)
                        .googleCalendarId("primary")
                        .googleEventId(event.id())
                        .lastSyncedAt(java.time.LocalDateTime.now())
                        .build();

                linkRepository.save(link);
                created++;
            }
        }

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

        // 종일 일정
        if (start.date() != null && !start.date().isBlank()) {
            LocalDate scheduleDate = LocalDate.parse(start.date());
            return new ParsedEvent(scheduleDate, null, null);
        }

        // 시간 지정 일정
        if (start.dateTime() != null && !start.dateTime().isBlank()) {
            OffsetDateTime startDateTime = OffsetDateTime.parse(start.dateTime());
            LocalDate scheduleDate = startDateTime.toLocalDate();
            LocalTime startTime = startDateTime.toLocalTime();

            LocalTime endTime = null;
            if (end != null && end.dateTime() != null && !end.dateTime().isBlank()) {
                endTime = OffsetDateTime.parse(end.dateTime()).toLocalTime();
            }

            return new ParsedEvent(scheduleDate, startTime, endTime);
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
            LocalDate scheduleDate,
            LocalTime startTime,
            LocalTime endTime
    ) {
    }
}
