package com.likelion.staycare.domain.user.controller;

import com.likelion.staycare.domain.user.dto.*;
import com.likelion.staycare.domain.user.service.UserService;
import com.likelion.staycare.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NicknameRequest request
    ) {
        return ResponseEntity.ok(userService.updateNickname(userDetails.getUserId(), request));
    }

    @PatchMapping("/goal")
    public ResponseEntity<UserResponse> updateGoal(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody GoalRequest request
            ) {
        return  ResponseEntity.ok(userService.updateGoal(userDetails.getUserId(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getUserId()));
    }
}
