package com.likelion.staycare.domain.schedule.repository;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByUserIdAndScheduleDate(Long userId, LocalDate scheduleDate);
}
