package com.likelion.staycare.domain.schedule.service;

import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarScheduleLinkRepository;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarSchedulePushService;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarScheduleSyncService;
import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleDateResponse;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import com.likelion.staycare.domain.schedule.exception.ScheduleNotFoundException;
import com.likelion.staycare.domain.schedule.repository.ScheduleRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GoogleCalendarSchedulePushService googleCalendarSchedulePushService;
    private final GoogleCalendarScheduleSyncService googleCalendarScheduleSyncService;
    private final GoogleCalendarScheduleLinkRepository googleCalendarScheduleLinkRepository;

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        validateDateRange(request.startDate(), request.endDate());

        User user = getUser(userId);

        Schedule schedule = scheduleRepository.save(
                Schedule.builder()
                        .user(user)
                        .title(request.title())
                        .startDate(request.startDate())
                        .endDate(request.endDate())
                        .startTime(request.startTime())
                        .endTime(request.endTime())
                        .companion(request.companion())
                        .category(request.category())
                        .build()
        );

        try {
            scheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.error("Schedule 저장 실패 - DB 제약조건 오류. userId={}, startDate={}, endDate={}",
                    userId, request.startDate(), request.endDate(), e);
            throw e;
        }

        try {
            googleCalendarSchedulePushService.createGoogleEventIfConnected(schedule);
        } catch (Exception e) {
            log.error("Google Calendar 일정 생성 실패. scheduleId={}", schedule.getId(), e);
        }

        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        validateDateRange(request.startDate(), request.endDate());

        getUser(userId);
        Schedule schedule = getOwnedSchedule(userId, scheduleId);

        schedule.updateSchedule(
                request.title(),
                request.startDate(),
                request.endDate(),
                request.startTime(),
                request.endTime(),
                request.companion(),
                request.category()
        );

        try {
            scheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.error("Schedule 수정 실패 - DB 제약조건 오류. scheduleId={}, userId={}, startDate={}, endDate={}",
                    scheduleId, userId, request.startDate(), request.endDate(), e);
            throw e;
        }

        try {
            googleCalendarSchedulePushService.updateGoogleEventIfLinked(schedule);
        } catch (Exception e) {
            log.error("Google Calendar 일정 수정 실패. scheduleId={}", schedule.getId(), e);
        }

        return ScheduleResponse.from(schedule);
    }

    public ScheduleResponse getTodaySchedule(Long userId) {
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);

        try {
            googleCalendarScheduleSyncService.syncDate(userId, today);
        } catch (Exception e) {
            log.error("Google Calendar 오늘 일정 동기화 실패. userId={}, date={}", userId, today, e);
        }

        return scheduleRepository
                .findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        today,
                        today,
                        ScheduleStatus.ACTIVE
                )
                .map(ScheduleResponse::from)
                .orElse(null);
    }

    public List<ScheduleDateResponse> getSchedulesByDate(Long userId, LocalDate date) {
        getUser(userId);

        try {
            googleCalendarScheduleSyncService.syncDate(userId, date);
        } catch (Exception e) {
            log.error("Google Calendar 날짜 일정 동기화 실패. userId={}, date={}", userId, date, e);
        }

        return scheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        date,
                        date,
                        ScheduleStatus.ACTIVE
                )
                .stream()
                .map(schedule -> ScheduleDateResponse.from(schedule, LocalDate.now(KOREA_ZONE_ID)))
                .toList();
    }

    @Transactional
    public ScheduleResponse cancelSchedule(Long userId, Long scheduleId) {
        getUser(userId);
        Schedule schedule = getOwnedSchedule(userId, scheduleId);

        schedule.cancel();

        try {
            scheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.error("Schedule 취소 상태 반영 실패. scheduleId={}", scheduleId, e);
            throw e;
        }

        try {
            googleCalendarSchedulePushService.cancelGoogleEventIfLinked(schedule);
        } catch (Exception e) {
            log.error("Google Calendar 일정 취소 실패. scheduleId={}", schedule.getId(), e);
        }

        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = getOwnedSchedule(userId, scheduleId);

        try {
            googleCalendarSchedulePushService.deleteGoogleEventIfLinked(schedule);
        } catch (Exception e) {
            log.error("Google Calendar 일정 삭제 실패. scheduleId={}", schedule.getId(), e);
        }

        googleCalendarScheduleLinkRepository.findBySchedule_Id(schedule.getId())
                .ifPresent(googleCalendarScheduleLinkRepository::delete);

        scheduleRepository.delete(schedule);
        scheduleRepository.flush();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private Schedule getOwnedSchedule(Long userId, Long scheduleId) {
        return scheduleRepository.findByIdAndUserId(scheduleId, userId)
                .orElseThrow(ScheduleNotFoundException::new);
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("시작일/종료일은 필수입니다.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작일은 종료일보다 늦을 수 없습니다.");
        }
    }
}
