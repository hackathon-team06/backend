package com.likelion.staycare.domain.googlecalendar.repository;

import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarScheduleLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleCalendarScheduleLinkRepository extends JpaRepository<GoogleCalendarScheduleLink, Long> {

    Optional<GoogleCalendarScheduleLink> findByUser_IdAndGoogleEventId(Long userId, String googleEventId);

    Optional<GoogleCalendarScheduleLink> findBySchedule_Id(Long scheduleId);
}
