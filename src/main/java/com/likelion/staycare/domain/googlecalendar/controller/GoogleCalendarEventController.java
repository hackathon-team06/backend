package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventsResponse;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarApiService;
import com.likelion.staycare.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarEventController {

    private final GoogleCalendarApiService googleCalendarApiService;

    @GetMapping("/events")
    public ResponseEntity<GoogleCalendarEventsResponse> getEvents(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        GoogleCalendarEventsResponse response =
                googleCalendarApiService.getEventsByDate(userDetails.getUserId(), date);

        return ResponseEntity.ok(response);
    }
}
