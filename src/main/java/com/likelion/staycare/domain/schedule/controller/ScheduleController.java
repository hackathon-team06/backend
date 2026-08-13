package com.likelion.staycare.domain.schedule.controller;

import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleDateResponse;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.service.ScheduleService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Schedule", description = "일정 API")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "일정 등록", description = "여러 날짜 범위를 포함하는 일정을 등록합니다.")
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ScheduleCreateRequest request
    ) {
        return ResponseEntity.ok(scheduleService.createSchedule(userDetails.getUserId(), request));
    }

    @Operation(summary = "일정 수정", description = "기존 일정의 날짜 범위와 상세 정보를 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ScheduleUpdateRequest request
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(userDetails.getUserId(), id, request));
    }

    @Operation(summary = "일정 취소", description = "기존 일정을 취소 상태로 변경합니다.")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScheduleResponse> cancelSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scheduleService.cancelSchedule(userDetails.getUserId(), id));
    }

    @Operation(summary = "오늘 일정 조회", description = "오늘 날짜가 범위 안에 포함되는 활성 일정을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ScheduleResponse> getTodaySchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(scheduleService.getTodaySchedule(userDetails.getUserId()));
    }

    @Operation(summary = "날짜별 일정 조회", description = "특정 날짜가 범위 안에 포함되는 일정 목록을 조회합니다.")
    @GetMapping("/date")
    public ResponseEntity<List<ScheduleDateResponse>> getSchedulesByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(userId, date));
    }

    @Operation(summary = "일정 삭제", description = "기존 일정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(userDetails.getUserId(), id);
        return ResponseEntity.ok().build();
    }
}
