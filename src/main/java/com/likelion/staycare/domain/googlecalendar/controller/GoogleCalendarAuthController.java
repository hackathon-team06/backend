package com.likelion.staycare.domain.googlecalendar.controller;

import com.likelion.staycare.domain.googlecalendar.dto.GoogleCalendarCallbackResponse;
import com.likelion.staycare.domain.googlecalendar.service.GoogleCalendarOAuthService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Tag(name = "GoogleCalendar", description = "구글 캘린더 연동 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/google-calendar")
public class GoogleCalendarAuthController {

    private final GoogleCalendarOAuthService googleCalendarOAuthService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Operation(summary = "구글 연동 URL 조회", description = "구글과 연동할 때 필요한 authorization URL을 제공합니다.")
    @GetMapping("/connect-url")
    public ResponseEntity<Map<String, String>> connectUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String authorizationUrl =
                googleCalendarOAuthService.createAuthorizationUrl(userDetails.getUserId());

        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
    }

    @Operation(summary = "구글 캘린더 OAuth 콜백", description = "구글 OAuth 인증 완료 후 프론트 페이지로 리다이렉트합니다.")
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error
    ) {
        GoogleCalendarCallbackResponse response =
                googleCalendarOAuthService.handleCallback(code, state, error);

        String redirectUrl;

        if (response.success()) {
            redirectUrl = frontendUrl + "/home?calendar=connected";
        } else {
            String message = URLEncoder.encode(response.message(), StandardCharsets.UTF_8);
            redirectUrl = frontendUrl + "/home?calendar=failed&message=" + message;
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }
}
