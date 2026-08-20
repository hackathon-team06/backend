package com.likelion.staycare.domain.notification.controller;

import com.likelion.staycare.domain.notification.dto.NotificationResponse;
import com.likelion.staycare.domain.notification.dto.PushTokenRequest;
import com.likelion.staycare.domain.notification.service.NotificationService;
import com.likelion.staycare.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/token")
    public ResponseEntity<NotificationResponse> savePushToken(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid PushTokenRequest request
    ) {
        notificationService.savePushToken(userDetails.getUserId(), request);
        return ResponseEntity.ok(new NotificationResponse("푸시 토큰이 저장되었습니다."));
    }

    @PostMapping("/test")
    public ResponseEntity<String> sendTestNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.sendTestNotification(userDetails.getUserId());
        return ResponseEntity.ok("테스트 알림 발송 완료");
    }

}
