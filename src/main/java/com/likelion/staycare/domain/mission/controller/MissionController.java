package com.likelion.staycare.domain.mission.controller;

import com.likelion.staycare.domain.mission.dto.request.EveningMissionCreateRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.service.MissionService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@Tag(name = "Mission", description = "미션 API")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "아침 미션 생성", description = "현재 로그인한 사용자의 아침 미션을 생성하거나 기존 미션을 반환합니다.")
    @PostMapping("/morning")
    public ResponseEntity<MorningMissionResponse> generateMorningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.generateMorningMission(userDetails.getUserId()));
    }

    @Operation(summary = "저녁 미션 생성", description = "현재 로그인한 사용자의 저녁 미션을 생성하거나 기존 미션을 반환합니다.")
    @PostMapping("/evening")
    public ResponseEntity<EveningMissionResponse> generateEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EveningMissionCreateRequest request
    ) {
        return ResponseEntity.ok(
                missionService.generateEveningMission(userDetails.getUserId(), request.skinCondition())
        );
    }

    @Operation(summary = "오늘 미션 조회", description = "오늘 생성된 아침/저녁 미션을 함께 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<TodayMissionResponse> getTodayMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getTodayMissions(userDetails.getUserId()));
    }

    @Operation(summary = "미션 단계 조회", description = "특정 미션의 단계 목록과 stepId, 완료 여부를 조회합니다.")
    @GetMapping("/{missionId}/steps")
    public ResponseEntity<List<MissionStepDetailResponse>> getMissionSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long missionId
    ) {
        return ResponseEntity.ok(missionService.getMissionSteps(userDetails.getUserId(), missionId));
    }

    @Operation(summary = "날짜별 미션 조회", description = "특정 사용자의 특정 날짜 미션 목록과 단계 목록을 함께 조회합니다.")
    @GetMapping("/date")
    public ResponseEntity<List<MissionByDateResponse>> getMissionsByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getMissionsByDate(userId, date));
    }

    @Operation(summary = "미션 단계 완료", description = "특정 미션 단계를 완료 처리합니다.")
    @PatchMapping("/steps/{stepId}")
    public ResponseEntity<Void> completeMissionStep(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stepId
    ) {
        missionService.completeStep(userDetails.getUserId(), stepId);
        return ResponseEntity.ok().build();
    }
}
