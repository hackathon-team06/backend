package com.likelion.staycare.domain.schedule.service;

import com.likelion.staycare.domain.googlecalendar.repository.GoogleCalendarScheduleLinkRepository;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarSchedulePushService;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final GoogleCalendarSchedulePushService googleCalendarSchedulePushService;
    private final GoogleCalendarScheduleLinkRepository googleCalendarScheduleLinkRepository;

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        User user = getUser(userId);

        Schedule schedule = Schedule.builder()
                .user(user)
                .title(request.title())
                .scheduleDate(request.scheduleDate())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .companion(request.companion())
                .category(request.category())
                .build();

        Schedule savedSchedule = scheduleRepository.save(schedule);

        // 로컬 DB 제약조건 오류를 Google 에러로 오해하지 않도록 먼저 flush
        try {
            scheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.error("Schedule 저장 실패 - DB 제약조건 오류. userId={}, date={}",
                    userId, request.scheduleDate(), e);
            throw e;
        }

        try {
            googleCalendarSchedulePushService.createGoogleEventIfConnected(savedSchedule);
        } catch (Exception e) {
            log.error("Google Calendar 일정 생성 실패. scheduleId={}", savedSchedule.getId(), e);
        }

        return ScheduleResponse.from(savedSchedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        getUser(userId);
        Schedule schedule = getOwnedSchedule(userId, scheduleId);

        schedule.updateSchedule(
                request.title(),
                request.scheduleDate(),
                request.startTime(),
                request.endTime(),
                request.companion(),
                request.category()
        );

        // 여기서 먼저 DB update를 확정시켜야
        // 중복 날짜 같은 로컬 오류가 Google 에러처럼 보이지 않음
        try {
            scheduleRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.error("Schedule 수정 실패 - DB 제약조건 오류. scheduleId={}, userId={}, date={}",
                    scheduleId, userId, request.scheduleDate(), e);
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
        return scheduleRepository.findByUserIdAndScheduleDateAndStatus(
                        userId,
                        LocalDate.now(),
                        ScheduleStatus.ACTIVE
                )
                .map(ScheduleResponse::from)
                .orElse(null);
    }

    public List<ScheduleDateResponse> getSchedulesByDate(Long userId, LocalDate date) {
        getUser(userId);

        return scheduleRepository.findAllByUserIdAndScheduleDateOrderByScheduleDateAsc(userId, date)
                .stream()
                .map(schedule -> ScheduleDateResponse.from(schedule, LocalDate.now()))
                .toList();
    }

    @Transactional
    public ScheduleResponse cancelSchedule(Long userId, Long scheduleId) {
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

        // Google 삭제 성공/실패와 별개로 로컬 링크 정리
        googleCalendarScheduleLinkRepository.findBySchedule_Id(schedule.getId())
                .ifPresent(googleCalendarScheduleLinkRepository::delete);

        scheduleRepository.delete(schedule);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private Schedule getOwnedSchedule(Long userId, Long scheduleId) {
        getUser(userId);

        return scheduleRepository.findById(scheduleId)
                .filter(schedule -> schedule.getUser().getId().equals(userId))
                .orElseThrow(ScheduleNotFoundException::new);
    }
}
