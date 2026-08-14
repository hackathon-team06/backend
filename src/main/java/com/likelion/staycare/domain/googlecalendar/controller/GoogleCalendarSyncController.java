package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarSyncResult;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarScheduleSyncService;
import com.likelion.staycare.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarSyncController {

    private final GoogleCalendarScheduleSyncService googleCalendarScheduleSyncService;

    @PostMapping("/sync")
    public ResponseEntity<GoogleCalendarSyncResult> syncDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        GoogleCalendarSyncResult result =
                googleCalendarScheduleSyncService.syncDate(userDetails.getUserId(), date);

        return ResponseEntity.ok(result);
    }
}
