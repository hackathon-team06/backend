package com.likelion.staycare.domain.mission.controller;

import com.likelion.staycare.domain.mission.dto.request.EveningMissionCreateRequest;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionStepAddRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSaveRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSurveySaveRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionOptionsResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineSurveyOptionsResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.WeeklyMissionStatusResponse;
import com.likelion.staycare.domain.mission.service.MissionService;
import com.likelion.staycare.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Mission", description = "Mission API")
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(
            summary = "2. AI 추천 대체용 생활 루틴 후보 7개 조회",
            operationId = "mission02GetMorningRoutineOptions",
            description = """
                    AI 추천 3개 중 마음에 들지 않는 항목이 있을 때 대체용으로 사용할 수 있는 생활 루틴 후보 7개를 조회합니다.
                    이 API는 선택 기록만 내려주고, 기록 결과를 미리 저장하지는 않습니다.
                    """
    )
    @GetMapping("/morning-routine/options")
    public ResponseEntity<MorningRoutineSurveyOptionsResponse> getMorningRoutineSurveyOptions() {
        return ResponseEntity.ok(missionService.getMorningRoutineSurveyOptions());
    }

    @Deprecated
    @Operation(
            summary = "Deprecated. 기존 생활 루틴 설문 저장",
            operationId = "missionDeprecatedSaveMorningRoutineSurvey",
            description = """
                    기존 호환성을 위해 유지되는 deprecated API입니다.
                    새 메인 플로우에서는 사용하지 않으며 AI 추천의 선행 조건도 아닙니다.
                    현재는 부작용 없이 200 OK만 반환합니다.
                    """
    )
    @PostMapping("/morning-routine/survey")
    public ResponseEntity<Void> saveMorningRoutineSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MorningRoutineSurveySaveRequest request
    ) {
        missionService.saveMorningRoutineSurvey(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "1. 아침 고정 미션 AI 추천 3개",
            operationId = "mission01RecommendMorningRoutine",
            description = """
                    설문 데이터 없이 사용자 정보와 진단 정보를 바탕으로 아침 고정 미션을 추천합니다.
                    현재 고정 미션이 0개면 3개, 1개면 2개, 2개면 1개를 추천합니다.
                    현재 3개면 추가 추천이 불가합니다.
                    categories는 선택적으로 추천 방향을 좁히는 용도입니다.
                    """
    )
    @PostMapping("/morning-routine/recommendations")
    public ResponseEntity<MorningRoutineRecommendationResponse> recommendMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MorningRoutineRecommendationRequest request
    ) {
        return ResponseEntity.ok(missionService.recommendMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "3. 아침 고정 미션 3개 선택 및 확정",
            operationId = "mission03SaveMorningRoutine",
            description = """
                    최종 고정 아침 미션 저장 API입니다.
                    AI 추천, 생활 루틴 후보 선택(SURVEY), 직접 입력(CUSTOM)을 조합해 저장할 수 있습니다.
                    최초에는 정확히 3개를 저장하고, 삭제 이후에는 부족한 개수만 추가 저장합니다.
                    저장된 최종 고정 미션은 항상 최대 3개입니다.
                    """
    )
    @PostMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> saveMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MorningRoutineSaveRequest request
    ) {
        return ResponseEntity.ok(missionService.saveMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "5. 오늘 아침 미션 생성 또는 조회",
            operationId = "mission05GenerateMorningMission",
            description = """
                    최종 확정된 고정 아침 미션 최대 3개를 기반으로 오늘의 MORNING GeneratedMission을 생성하거나 기존 생성본을 반환합니다.
                    일정이 있으면 추가 step 1개가 붙을 수 있습니다.
                    오후에 처음 생성하면 기존 정책대로 FAILED 처리됩니다.
                    """
    )
    @PostMapping("/morning")
    public ResponseEntity<MorningMissionResponse> generateMorningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.generateMorningMission(userDetails.getUserId()));
    }

    @Operation(
            summary = "6. 미션 step 완료 처리",
            operationId = "mission06CompleteMissionStep",
            description = """
                    GeneratedMission의 특정 step 완료 처리 API입니다.
                    stepId는 오늘 미션 생성/조회 또는 미션 step 상세 조회 응답에서 받은 값을 사용합니다.
                    동일 step 재요청 시에도 중복 처리 없이 기존 완료 상태만 유지합니다.
                    """
    )
    @PatchMapping("/steps/{stepId}")
    public ResponseEntity<Void> completeMissionStep(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "완료 처리할 GeneratedMissionStep ID", example = "101")
            @PathVariable Long stepId
    ) {
        missionService.completeStep(userDetails.getUserId(), stepId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "7. 고정 아침 미션 삭제",
            operationId = "mission07DeleteMorningRoutineItem",
            description = """
                    현재 고정 아침 미션 항목 1개를 삭제합니다.
                    삭제 후에는 item_order가 1..N으로 즉시 재정렬됩니다.
                    이후 필요하면 AI 추천 또는 생활 루틴 후보로 부족한 개수만 보충하면 됩니다.
                    """
    )
    @DeleteMapping("/morning-routine/items/{itemId}")
    public ResponseEntity<MorningRoutineResponse> deleteMorningRoutineItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 고정 아침 미션 item ID", example = "12")
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(missionService.deleteMorningRoutineItem(userDetails.getUserId(), itemId));
    }

    @Operation(
            summary = "8. 저녁 상태 입력 및 저녁 미션 생성",
            operationId = "mission08GenerateEveningMission",
            description = "저녁 미션 플로우의 시작 API입니다."
    )
    @PostMapping("/evening")
    public ResponseEntity<EveningMissionResponse> generateEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EveningMissionCreateRequest request
    ) {
        return ResponseEntity.ok(missionService.generateEveningMission(userDetails.getUserId(), request.conditions()));
    }

    @Operation(
            summary = "8-1. 오늘 저녁 미션 조회",
            operationId = "mission08GetEveningMission",
            description = "오늘 생성된 저녁 미션이 있으면 조회합니다. 없으면 MISSION_NOT_FOUND를 반환합니다."
    )
    @GetMapping("/evening")
    public ResponseEntity<EveningMissionResponse> getEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getEveningMission(userDetails.getUserId()));
    }

    @Operation(
            summary = "8-3. 저녁 미션 카테고리 선택 기반 AI 재추천",
            operationId = "mission08RecommendEveningMission",
            description = """
                    삭제 후 부족한 저녁 미션을 AI로 다시 추천받습니다.
                    카테고리는 최대 2개까지 선택할 수 있으며,
                    추천 개수는 카테고리 개수가 아니라 현재 비어 있는 저녁 미션 슬롯 수에 따라 결정됩니다.
                    categories 예시: {"categories":["MOISTURE","SOOTHING_BARRIER"]}
                    categories 빈 배열 예시: {"categories":[]}
                    """
    )
    @PostMapping("/evening/recommendations")
    public ResponseEntity<EveningMissionRecommendationResponse> recommendEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EveningMissionRecommendationRequest request
    ) {
        return ResponseEntity.ok(missionService.recommendEveningMission(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "8-2. 저녁 미션 step 삭제",
            operationId = "mission08DeleteEveningMissionStep",
            description = "오늘 저녁 미션의 수정 가능한 step 1개를 삭제합니다. 일정 기반 자동 추가 step은 삭제 대상이 아닙니다."
    )
    @DeleteMapping("/evening/steps/{stepId}")
    public ResponseEntity<EveningMissionResponse> deleteEveningMissionStep(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "삭제할 GeneratedMissionStep ID", example = "201")
            @PathVariable Long stepId
    ) {
        return ResponseEntity.ok(missionService.deleteEveningMissionStep(userDetails.getUserId(), stepId));
    }

    @Operation(
            summary = "8-4. 저녁 미션 step 추가",
            operationId = "mission08AddEveningMissionStep",
            description = "AI 재추천 결과를 선택하거나 직접 입력해서 비어 있는 저녁 미션 슬롯에 추가합니다."
    )
    @PostMapping("/evening/steps")
    public ResponseEntity<EveningMissionResponse> addEveningMissionSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody EveningMissionStepAddRequest request
    ) {
        return ResponseEntity.ok(missionService.addEveningMissionSteps(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "오늘 미션 조회",
            operationId = "mission09GetTodayMissions",
            description = "오늘 생성된 아침 미션과 저녁 미션을 한 번에 조회합니다."
    )
    @GetMapping("/today")
    public ResponseEntity<TodayMissionResponse> getTodayMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getTodayMissions(userDetails.getUserId()));
    }

    @Operation(
            summary = "미션 공통 옵션 조회",
            operationId = "mission90GetMissionOptions",
            description = "아침 카테고리와 저녁 상태 옵션을 조회합니다."
    )
    @GetMapping("/options")
    public ResponseEntity<MissionOptionsResponse> getMissionOptions() {
        return ResponseEntity.ok(missionService.getMissionOptions());
    }

    @Operation(
            summary = "4. 현재 고정 아침 미션 조회",
            operationId = "mission04GetMorningRoutine",
            description = """
                    현재 최종 확정된 고정 아침 미션을 조회합니다.
                    AI, SURVEY, CUSTOM 출처를 모두 반환합니다.
                    응답의 itemId는 삭제 API에서 사용합니다.
                    """
    )
    @GetMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> getMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getMorningRoutine(userDetails.getUserId()));
    }

    @Operation(
            summary = "특정 미션 step 상세 조회",
            operationId = "mission92GetMissionSteps",
            description = "특정 GeneratedMission의 step 목록과 완료 여부를 상세 조회합니다."
    )
    @GetMapping("/{missionId}/steps")
    public ResponseEntity<List<MissionStepDetailResponse>> getMissionSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 GeneratedMission ID", example = "55")
            @PathVariable Long missionId
    ) {
        return ResponseEntity.ok(missionService.getMissionSteps(userDetails.getUserId(), missionId));
    }

    @Operation(
            summary = "날짜별 미션 조회",
            operationId = "mission93GetMissionsByDate",
            description = "특정 날짜에 생성된 미션 목록을 조회합니다."
    )
    @GetMapping("/date")
    public ResponseEntity<List<MissionByDateResponse>> getMissionsByDate(
            @Parameter(description = "조회할 사용자 ID", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "조회할 날짜. yyyy-MM-dd", example = "2026-08-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getMissionsByDate(userId, date));
    }

    @Operation(
            summary = "주간 미션 완료 현황 조회",
            operationId = "mission94GetWeeklyMissionStatus",
            description = "기준 날짜가 포함된 주간 미션 완료 현황을 조회합니다."
    )
    @GetMapping("/week")
    public ResponseEntity<WeeklyMissionStatusResponse> getWeeklyMissionStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "기준 날짜", example = "2026-07-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getWeeklyMissionStatus(userDetails.getUserId(), date));
    }
}
