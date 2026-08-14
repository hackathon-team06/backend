package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarEventsResponse;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarApiService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "GoogleCalender Schedule", description = "구글캘린더 스케줄 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarEventController {

    private final GoogleCalendarApiService googleCalendarApiService;

    @Operation(summary = "구글 켈린더 스케줄 조회 API", description = "생성한 스케줄을 조회합니다.")
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
