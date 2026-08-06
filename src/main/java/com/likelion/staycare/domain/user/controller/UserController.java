package com.likelion.staycare.domain.user.controller;

import com.likelion.staycare.domain.user.dto.*;
import com.likelion.staycare.domain.user.service.UserService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 로그인", description = "테스트 계정 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "닉네임 수정(JWT 필요)", description = "현재 로그인한 사용자의 닉네임 수정")
    @PatchMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody NicknameRequest request
    ) {
        return ResponseEntity.ok(userService.updateNickname(userDetails.getUserId(), request));
    }

    @Operation(summary = "목표 수정(JWT 필요)", description = "현재 로그인한 사용자의 목표 수정")
    @PatchMapping("/goal")
    public ResponseEntity<UserResponse> updateGoal(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody GoalRequest request
            ) {
        return  ResponseEntity.ok(userService.updateGoal(userDetails.getUserId(), request));
    }

    @Operation(summary = "내 정보 조회(JWT 필요)", description = "현재 로그인한 사용자의 마이페이지 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getUserId()));
    }
}
