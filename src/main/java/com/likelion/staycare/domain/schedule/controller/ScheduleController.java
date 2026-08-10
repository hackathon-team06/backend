package com.likelion.staycare.domain.schedule.controller;

import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.service.ScheduleService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Schedule", description = "일정 API")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 등록", description = "현재 로그인한 사용자의 오늘 일정을 등록합니다.")
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ScheduleCreateRequest request
    ) {
        return ResponseEntity.ok(scheduleService.createSchedule(userDetails.getUserId(), request));
    }

    @Operation(summary = "오늘 일정 조회", description = "현재 로그인한 사용자의 오늘 일정을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ScheduleResponse> getTodaySchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(scheduleService.getTodaySchedule(userDetails.getUserId()));
    }

    @Operation(summary = "일정 삭제", description = "현재 로그인한 사용자의 일정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(userDetails.getUserId(), id);
        return ResponseEntity.ok().build();
    }
}
