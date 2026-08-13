package com.likelion.staycare.domain.schedule.service;

import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleDateResponse;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import com.likelion.staycare.domain.schedule.exception.ScheduleErrorCode;
import com.likelion.staycare.domain.schedule.exception.ScheduleNotFoundException;
import com.likelion.staycare.domain.schedule.repository.ScheduleRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        User user = getUser(userId);
        validateNoOverlap(userId, request.startDate(), request.endDate(), null);

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

        return ScheduleResponse.from(schedule);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long userId, Long scheduleId, ScheduleUpdateRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        getUser(userId);
        Schedule schedule = getOwnedSchedule(userId, scheduleId);
        validateNoOverlap(userId, request.startDate(), request.endDate(), scheduleId);

        schedule.updateSchedule(
                request.title(),
                request.startDate(),
                request.endDate(),
                request.startTime(),
                request.endTime(),
                request.companion(),
                request.category()
        );

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    public ScheduleResponse getTodaySchedule(Long userId) {
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);
        return scheduleRepository.findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
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

        return scheduleRepository.findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                        userId,
                        date,
                        date
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
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = getOwnedSchedule(userId, scheduleId);
        scheduleRepository.delete(schedule);
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
        if (startDate.isAfter(endDate)) {
            throw new CustomException(ScheduleErrorCode.INVALID_SCHEDULE_DATE_RANGE);
        }
    }

    private void validateNoOverlap(Long userId, LocalDate startDate, LocalDate endDate, Long excludeScheduleId) {
        boolean overlapped = scheduleRepository
                .findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        endDate,
                        startDate,
                        ScheduleStatus.ACTIVE
                )
                .stream()
                .anyMatch(schedule -> excludeScheduleId == null || !schedule.getId().equals(excludeScheduleId));

        if (overlapped) {
            throw new CustomException(ScheduleErrorCode.SCHEDULE_DATE_CONFLICT);
        }
    }
}