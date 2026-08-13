package com.likelion.staycare.domain.schedule.repository;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ScheduleStatus status
    );

    List<Schedule> findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Schedule> findAllByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            ScheduleStatus status
    );

    Optional<Schedule> findByIdAndUserId(Long scheduleId, Long userId);
}
