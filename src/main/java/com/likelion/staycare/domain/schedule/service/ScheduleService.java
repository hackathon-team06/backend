package com.likelion.staycare.domain.schedule.service;

import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.entity.Schedule;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponse createSchedule(Long userId, ScheduleCreateRequest request) {
        User user = getUser(userId);

        Schedule schedule = scheduleRepository.findByUserIdAndScheduleDate(userId, request.scheduleDate())
                .orElseGet(() -> Schedule.builder()
                        .user(user)
                        .scheduleDate(request.scheduleDate())
                        .companion(request.companion())
                        .category(request.category())
                        .build());

        schedule.updateSchedule(request.companion(), request.category());
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    public ScheduleResponse getTodaySchedule(Long userId) {
        return scheduleRepository.findByUserIdAndScheduleDate(userId, LocalDate.now())
                .map(ScheduleResponse::from)
                .orElse(null);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .filter(savedSchedule -> savedSchedule.getUser().getId().equals(userId))
                .orElseThrow(ScheduleNotFoundException::new);

        scheduleRepository.delete(schedule);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }
}
