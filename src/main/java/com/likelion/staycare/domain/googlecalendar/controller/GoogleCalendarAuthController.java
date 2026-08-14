package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarOAuthService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "GoogleCalender", description = "구글캘린더 연동 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarAuthController {

    private final GoogleCalendarOAuthService googleCalendarOAuthService;

    @Operation(summary = "구글 연동 조회", description = "구글과 연동할 때 필요한 url을 제공합니다.")
    @GetMapping("/connect-url")
    public ResponseEntity<Map<String, String>> connectUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String authorizationUrl =
                googleCalendarOAuthService.createAuthorizationUrl(userDetails.getUserId());

        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
    }

}
