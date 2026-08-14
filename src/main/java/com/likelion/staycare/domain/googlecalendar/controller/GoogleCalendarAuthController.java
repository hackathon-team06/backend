package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarOAuthService;
import com.likelion.staycare.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarAuthController {

    private final GoogleCalendarOAuthService googleCalendarOAuthService;

    @GetMapping("/connect-url")
    public ResponseEntity<Map<String, String>> connectUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String authorizationUrl =
                googleCalendarOAuthService.createAuthorizationUrl(userDetails.getUserId());

        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
    }

    @GetMapping("/callback")
    public ResponseEntity<GoogleCalendarCallbackResponse> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error
    ) {
        GoogleCalendarCallbackResponse response =
                googleCalendarOAuthService.handleCallback(code, state, error);

        return ResponseEntity.ok(response);
    }
}
