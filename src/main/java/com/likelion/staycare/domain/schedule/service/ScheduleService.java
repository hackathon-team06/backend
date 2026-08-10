package com.likelion.staycare.domain.schedule.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        User user = getUser(userId);

        Schedule schedule = scheduleRepository
                .findByUserIdAndScheduleDate(userId, request.scheduleDate())
                .orElseGet(() -> Schedule.builder()
                        .user(user)
                        .title(request.title())
                        .scheduleDate(request.scheduleDate())
                        .startTime(request.startTime())
                        .endTime(request.endTime())
                        .companion(request.companion())
                        .category(request.category())
                        .build());

        schedule.updateSchedule(
                request.title(),
                request.scheduleDate(),
                request.startTime(),
                request.endTime(),
                request.companion(),
                request.category()
        );

        return ScheduleResponse.from(scheduleRepository.save(schedule));
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

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    public ScheduleResponse getTodaySchedule(Long userId) {
        return scheduleRepository.findByUserIdAndScheduleDateAndStatus(userId, LocalDate.now(), ScheduleStatus.ACTIVE)
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
}
