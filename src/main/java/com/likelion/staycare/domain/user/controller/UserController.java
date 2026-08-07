package com.likelion.staycare.domain.user.controller;

import com.likelion.staycare.domain.user.dto.*;
import com.likelion.staycare.domain.user.service.UserService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 로그인", description = "테스트 계정 로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "닉네임 수정(토큰 필요)", description = "현재 로그인한 사용자의 닉네임 수정")
    @PatchMapping("/nickname")
    public ResponseEntity<UserResponse> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @org.springframework.web.bind.annotation.RequestBody NicknameRequest request
    ) {
        return ResponseEntity.ok(userService.updateNickname(userDetails.getUserId(), request));
    }

    @Operation(summary = "목표 수정(토큰 필요)", description = "현재 로그인한 사용자의 목표 수정")
    @PatchMapping("/goal")
    public ResponseEntity<UserResponse> updateGoal(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @org.springframework.web.bind.annotation.RequestBody GoalRequest request
    ) {
        return ResponseEntity.ok(userService.updateGoal(userDetails.getUserId(), request));
    }

    @Operation(summary = "내 정보 조회(토큰 필요)", description = "현재 로그인한 사용자의 마이페이지 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.getMyInfo(userDetails.getUserId()));
    }

    @Operation(summary = "알림 설정 수정")
    @PatchMapping("/notification-setting")
    public ResponseEntity<UserResponse> updateNotificationSetting(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @org.springframework.web.bind.annotation.RequestBody NotificationSettingRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateNotificationSetting(userDetails.getUserId(), request)
        );
    }

    @Operation(summary = "프로필 이미지 업로드")
    @RequestBody(
            required = true,
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = ProfileImageUploadRequest.class)
            )
    )
    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(
                userService.updateProfileImage(userDetails.getUserId(), image)
        );
    }

    @Operation(summary = "프로필 이미지 삭제")
    @DeleteMapping("/profile-image")
    public ResponseEntity<UserResponse> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                userService.deleteProfileImage(userDetails.getUserId())
        );
    }

    @Schema(name = "ProfileImageUploadRequest", description = "프로필 이미지 업로드 form-data")
    static class ProfileImageUploadRequest {
        @Schema(type = "string", format = "binary", description = "업로드할 이미지 파일")
        public MultipartFile image;
    }
}
