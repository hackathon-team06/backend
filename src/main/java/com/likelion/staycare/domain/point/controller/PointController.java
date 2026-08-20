package com.likelion.staycare.domain.point.controller;

import com.likelion.staycare.domain.point.dto.response.PointResponse;
import com.likelion.staycare.domain.point.service.PointService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Point", description = "포인트 API")
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @Operation(summary = "보유 포인트 조회", description = "현재 로그인한 사용자의 누적 포인트를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<PointResponse> getMyPoint(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(pointService.getMyPoint(userDetails.getUserId()));
    }
}
