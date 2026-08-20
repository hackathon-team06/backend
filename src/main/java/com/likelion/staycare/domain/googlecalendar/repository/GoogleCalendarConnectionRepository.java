package com.likelion.staycare.domain.googlecalendar.repository;

import com.likelion.staycare.domain.googlecalendar.entity.GoogleCalendarConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleCalendarConnectionRepository extends JpaRepository<GoogleCalendarConnection, Long> {

    Optional<GoogleCalendarConnection> findByUser_Id(Long userId);
}
