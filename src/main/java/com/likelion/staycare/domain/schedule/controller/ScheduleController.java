package com.likelion.staycare.domain.schedule.controller;

import com.likelion.staycare.domain.schedule.dto.request.ScheduleCreateRequest;
import com.likelion.staycare.domain.schedule.dto.request.ScheduleUpdateRequest;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleDateResponse;
import com.likelion.staycare.domain.schedule.dto.response.ScheduleResponse;
import com.likelion.staycare.domain.schedule.service.ScheduleService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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

    @Operation(
            summary = "일정 등록",
            description = """
                    현재 로그인한 사용자의 일정을 등록합니다.
                    companion 선택값:
                    - ALONE = 혼자
                    - FAMILY = 가족/친척
                    - FRIEND = 친구
                    - LOVER = 연인
                    - COWORKER = 직장동료
                    - ACQUAINTANCE = 지인/모임

                    category 선택값:
                    - DATE = 데이트
                    - MEETING = 미팅/면접
                    - SELF_CARE = 자기관리
                    - DRINKING = 술자리/모임
                    - TRAVEL = 여행
                    - WEDDING = 결혼식
                    - EVENT = 이벤트
                    - TALK = 친목/수다
                    - CEREMONY = 행사
                    """
    )
    @RequestBody(
            required = true,
            content = @Content(
                    examples = {
                            @ExampleObject(
                                    name = "결혼식 일정",
                                    summary = "FRIEND + WEDDING",
                                    value = """
                                            {
                                              "title": "친구 결혼식",
                                              "scheduleDate": "2026-08-15",
                                              "startTime": "13:00",
                                              "endTime": "16:00",
                                              "companion": "FRIEND",
                                              "category": "WEDDING"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "데이트 일정",
                                    summary = "LOVER + DATE",
                                    value = """
                                            {
                                              "title": "연인과 데이트",
                                              "scheduleDate": "2026-08-12",
                                              "startTime": "18:30",
                                              "endTime": "21:00",
                                              "companion": "LOVER",
                                              "category": "DATE"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "미팅 일정",
                                    summary = "COWORKER + MEETING",
                                    value = """
                                            {
                                              "title": "직장동료와 미팅",
                                              "scheduleDate": "2026-08-11",
                                              "startTime": "10:00",
                                              "endTime": "11:30",
                                              "companion": "COWORKER",
                                              "category": "MEETING"
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "혼자 자기관리 일정",
                                    summary = "ALONE + SELF_CARE",
                                    value = """
                                            {
                                              "title": "혼자 자기관리",
                                              "scheduleDate": "2026-08-10",
                                              "startTime": "20:00",
                                              "endTime": "21:00",
                                              "companion": "ALONE",
                                              "category": "SELF_CARE"
                                            }
                                            """
                            )
                    }
            )
    )
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @org.springframework.web.bind.annotation.RequestBody ScheduleCreateRequest request
    ) {
        return ResponseEntity.ok(scheduleService.createSchedule(userDetails.getUserId(), request));
    }

    @Operation(summary = "일정 수정", description = "현재 로그인한 사용자의 일정을 수정합니다.")
    @RequestBody(
            required = true,
            content = @Content(
                    examples = @ExampleObject(
                            name = "일정 수정 예시",
                            value = """
                                    {
                                      "title": "가족 여행",
                                      "scheduleDate": "2026-08-20",
                                      "startTime": "09:00",
                                      "endTime": "20:00",
                                      "companion": "FAMILY",
                                      "category": "TRAVEL"
                                    }
                                    """
                    )
            )
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ScheduleUpdateRequest request
    ) {
        return ResponseEntity.ok(scheduleService.updateSchedule(userDetails.getUserId(), id, request));
    }

    @Operation(summary = "일정 취소", description = "현재 로그인한 사용자의 일정을 취소 상태로 변경합니다.")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScheduleResponse> cancelSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scheduleService.cancelSchedule(userDetails.getUserId(), id));
    }

    @Operation(summary = "오늘 일정 조회", description = "현재 로그인한 사용자의 오늘 활성 일정을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<ScheduleResponse> getTodaySchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(scheduleService.getTodaySchedule(userDetails.getUserId()));
    }

    @Operation(summary = "날짜별 일정 조회", description = "특정 사용자의 특정 날짜 일정 목록을 조회합니다.")
    @GetMapping("/date")
    public ResponseEntity<List<ScheduleDateResponse>> getSchedulesByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(userId, date));
    }

    @Operation(summary = "일정 삭제", description = "현재 로그인한 사용자의 일정을 실제로 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        scheduleService.deleteSchedule(userDetails.getUserId(), id);
        return ResponseEntity.ok().build();
    }
}
