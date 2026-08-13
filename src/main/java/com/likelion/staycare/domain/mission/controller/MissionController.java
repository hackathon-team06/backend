package com.likelion.staycare.domain.mission.controller;

import com.likelion.staycare.domain.mission.dto.request.EveningMissionCreateRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSaveRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionOptionsResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineResponse;
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

@Tag(name = "Mission", description = "미션 API")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "미션 옵션 조회", description = "아침 루틴 카테고리와 저녁 귀가 후 상태 목록을 조회합니다.")
    @GetMapping("/options")
    public ResponseEntity<MissionOptionsResponse> getMissionOptions() {
        return ResponseEntity.ok(missionService.getMissionOptions());
    }

    @Operation(summary = "아침 루틴 추천", description = "사용자 정보와 선택한 카테고리를 바탕으로 고정 아침 루틴 후보를 추천합니다. 카테고리는 최대 3개까지 선택할 수 있습니다.")
    @PostMapping("/morning-routine/recommendations")
    public ResponseEntity<MorningRoutineRecommendationResponse> recommendMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MorningRoutineRecommendationRequest request
    ) {
        return ResponseEntity.ok(missionService.recommendMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(summary = "아침 루틴 저장", description = "추천 후보 또는 직접 입력한 미션으로 고정 아침 루틴 3개를 저장합니다.")
    @PostMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> saveMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MorningRoutineSaveRequest request
    ) {
        return ResponseEntity.ok(missionService.saveMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(summary = "아침 루틴 조회", description = "현재 로그인 사용자의 고정 아침 루틴을 조회합니다.")
    @GetMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> getMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getMorningRoutine(userDetails.getUserId()));
    }

    @Operation(summary = "아침 루틴 항목 삭제", description = "고정 아침 루틴의 특정 항목을 삭제합니다. 삭제 후에는 다시 추천받아 3개로 맞출 수 있습니다.")
    @DeleteMapping("/morning-routine/items/{itemId}")
    public ResponseEntity<MorningRoutineResponse> deleteMorningRoutineItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(missionService.deleteMorningRoutineItem(userDetails.getUserId(), itemId));
    }

    @Operation(summary = "오늘 아침 미션 생성", description = "저장된 고정 아침 루틴으로 오늘 아침 미션을 생성합니다.")
    @PostMapping("/morning")
    public ResponseEntity<MorningMissionResponse> generateMorningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.generateMorningMission(userDetails.getUserId()));
    }

    @Operation(summary = "오늘 저녁 미션 생성", description = "피부 타입, 아침 수행 여부, 귀가 후 상태, 오늘 일정을 바탕으로 저녁 웰니스 미션 3개를 생성합니다. NONE은 단독 선택만 가능합니다.")
    @PostMapping("/evening")
    public ResponseEntity<EveningMissionResponse> generateEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EveningMissionCreateRequest request
    ) {
        return ResponseEntity.ok(missionService.generateEveningMission(userDetails.getUserId(), request.conditions()));
    }

    @Operation(summary = "오늘 미션 조회", description = "오늘 생성된 아침 미션과 저녁 미션을 조회합니다. 저녁 시간 이후 아침 미션이 없으면 자동 미완료 상태로 생성될 수 있습니다.")
    @GetMapping("/today")
    public ResponseEntity<TodayMissionResponse> getTodayMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getTodayMissions(userDetails.getUserId()));
    }

    @Operation(summary = "미션 단계 조회", description = "특정 미션의 stepId, 순서, 완료 여부를 조회합니다.")
    @GetMapping("/{missionId}/steps")
    public ResponseEntity<List<MissionStepDetailResponse>> getMissionSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long missionId
    ) {
        return ResponseEntity.ok(missionService.getMissionSteps(userDetails.getUserId(), missionId));
    }

    @Operation(summary = "날짜별 미션 조회", description = "특정 날짜의 미션 목록과 단계 목록을 조회합니다.")
    @GetMapping("/date")
    public ResponseEntity<List<MissionByDateResponse>> getMissionsByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getMissionsByDate(userId, date));
    }

    @Operation(summary = "미션 단계 완료", description = "미션 단계를 완료 처리합니다. 아침 미션 시간이 지난 단계나 자동 미완료 처리된 단계는 완료할 수 없습니다.")
    @PatchMapping("/steps/{stepId}")
    public ResponseEntity<Void> completeMissionStep(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long stepId
    ) {
        missionService.completeStep(userDetails.getUserId(), stepId);
        return ResponseEntity.ok().build();
    }
}
