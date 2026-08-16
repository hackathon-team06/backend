package com.likelion.staycare.domain.mission.controller;

import com.likelion.staycare.domain.mission.dto.request.EveningMissionCreateRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSaveRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSurveySaveRequest;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(
        name = "Mission",
        description = """
                Mission API 전체 흐름:
                회원가입/진단 완료 -> 최초 생활 루틴 설문 -> AI 아침 미션 추천 -> 고정 아침 미션 확정 -> 매일 아침 GeneratedMission 사용

                이후 수정 흐름:
                고정 아침 미션 조회 -> 특정 itemId 삭제 -> 원하는 category로 AI 재추천 -> 부족한 개수만큼 다시 저장

                저녁 흐름:
                현재 상태 선택 -> 저녁 AI 미션 3개 생성 -> stepId 기준으로 완료 처리
                """
)
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @Operation(
            summary = "아침 생활 루틴 설문 선택지 조회",
            operationId = "mission01GetMorningRoutineSurveyOptions",
            description = """
                    회원가입 또는 진단 완료 후, 최초 생활 루틴 설문을 시작하기 전에 호출합니다.
                    선행 API는 없습니다.
                    Swagger 응답의 items에서 설문 코드와 한글 라벨을 함께 확인할 수 있습니다.
                    다음 단계에서는 /api/missions/morning-routine/survey 로 최대 3개를 저장합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "설문 선택지 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "items": [
                                                { "code": "WATER_AFTER_WAKEUP", "label": "기상 직후 미온수 한 잔 마시기" },
                                                { "code": "QUICK_WASH_AFTER_RETURN", "label": "귀가 후 10분 이내에 세안하기" },
                                                { "code": "NIGHT_STRETCHING", "label": "잠들기 전 스트레칭으로 혈액순환 돕기" },
                                                { "code": "TAKE_SKIN_SUPPLEMENT", "label": "피부 영양제 챙겨 먹기" },
                                                { "code": "CARRY_LIPBALM_SUNSTICK", "label": "립밤/선스틱 가방에 챙기기" },
                                                { "code": "MORNING_VENTILATION", "label": "아침 환기 5분 시키기" },
                                                { "code": "APPLY_SUNSCREEN_BEFORE_OUTING", "label": "외출 전 선크림 바르기" }
                                              ],
                                              "maxSelections": 3
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/morning-routine/options")
    public ResponseEntity<MorningRoutineSurveyOptionsResponse> getMorningRoutineSurveyOptions() {
        return ResponseEntity.ok(missionService.getMorningRoutineSurveyOptions());
    }

    @Operation(
            summary = "최초 생활 루틴 설문 저장",
            operationId = "mission02SaveMorningRoutineSurvey",
            description = """
                    최초 1회만 저장하는 생활 루틴 설문 API입니다.
                    실제 고정 아침 미션을 저장하는 API가 아니며, AI가 아침 미션을 추천할 때 참고하는 설문 데이터입니다.
                    먼저 01번 설문 선택지 조회 API를 호출한 뒤, items에 설문 코드 1~3개를 배열로 전달합니다.
                    이미 설문을 저장한 사용자는 다시 저장할 수 없으며 4xx 응답이 반환됩니다.
                    다음 단계에서는 /api/missions/morning-routine/recommendations 를 호출해 AI 추천을 받습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 저장 성공"),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 설문이 완료된 사용자",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "code": "MISSION4091",
                                              "message": "이미 아침 루틴 설문이 완료되었습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/morning-routine/survey")
    public ResponseEntity<Void> saveMorningRoutineSurvey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "설문 코드 1~3개를 배열로 전달합니다. 문자열 배열이 아니라 items 필드를 가진 JSON 객체여야 합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "최초 설문 저장 예시",
                                    value = """
                                            {
                                              "items": [
                                                "APPLY_SUNSCREEN_BEFORE_OUTING",
                                                "NIGHT_STRETCHING",
                                                "WATER_AFTER_WAKEUP"
                                              ]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody MorningRoutineSurveySaveRequest request
    ) {
        missionService.saveMorningRoutineSurvey(userDetails.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "아침 고정 미션 AI 추천",
            operationId = "mission03RecommendMorningRoutine",
            description = """
                    최초 아침 미션 추천과, 기존 고정 미션 삭제 후 부족한 개수만큼 재추천받는 경우 모두 사용하는 API입니다.
                    선행 API는 최초 설문 저장 API이며, 삭제 후 재추천 시에는 삭제 API 호출 후 사용합니다.
                    categories에는 어떤 종류의 새 미션을 추천받고 싶은지 선택적으로 전달합니다.

                    추천 개수 규칙:
                    현재 고정 미션 0개 -> 3개 추천
                    현재 고정 미션 1개 -> 2개 추천
                    현재 고정 미션 2개 -> 1개 추천

                    다음 단계에서는 추천 결과를 사용해 /api/missions/morning-routine 으로 실제 고정 아침 미션을 저장합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "recommendations": [
                                                "세안 후 3분 안에 보습제를 충분히 바르기",
                                                "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                "아침에 물 한 컵을 마시고 환기 5분 하기"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "현재 고정 미션이 이미 3개이거나 category 개수가 잘못된 경우")
    })
    @PostMapping("/morning-routine/recommendations")
    public ResponseEntity<MorningRoutineRecommendationResponse> recommendMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "최초 추천 또는 삭제 후 재추천 시 원하는 category를 최대 3개까지 선택적으로 전달합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "최초 추천 또는 2개 삭제 후 2개 추천 요청",
                                            value = """
                                                    {
                                                      "categories": [
                                                        "MOISTURE",
                                                        "SUN_PROTECTION"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "카테고리 없이 추천 요청",
                                            value = """
                                                    {
                                                      "categories": []
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MorningRoutineRecommendationRequest request
    ) {
        return ResponseEntity.ok(missionService.recommendMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "아침 고정 미션 선택 및 확정",
            operationId = "mission04SaveMorningRoutine",
            description = """
                    AI 추천 결과 또는 사용자가 직접 작성한 문구를 실제 고정 아침 미션으로 저장합니다.
                    MorningRoutineSurvey와 다른 개념이며, 여기서 저장한 항목이 매일 아침 반복 사용됩니다.

                    요청 규칙:
                    현재 고정 미션 0개 -> 정확히 3개 요청
                    현재 고정 미션 1개 -> 부족한 2개 요청
                    현재 고정 미션 2개 -> 부족한 1개 요청
                    현재 고정 미션 3개 -> 추가 불가

                    AI 추천 항목을 선택했다면 source=AI, 사용자가 직접 작성했다면 source=CUSTOM을 사용합니다.
                    CUSTOM일 때 category는 null로 보낼 수 있습니다.
                    items는 문자열 배열이 아니라 content/category/source를 가진 객체 배열입니다.
                    다음 단계에서는 /api/missions/morning 으로 오늘 아침 미션을 생성합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "고정 아침 미션 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "최초 3개 확정 응답 예시",
                                            value = """
                                                    {
                                                      "routineId": 1,
                                                      "items": [
                                                        {
                                                          "itemId": 11,
                                                          "content": "세안 후 3분 안에 보습제를 충분히 발라 수분을 잠그기",
                                                          "category": "수분/보습",
                                                          "source": "AI",
                                                          "itemOrder": 1
                                                        },
                                                        {
                                                          "itemId": 12,
                                                          "content": "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                          "category": "자외선 차단",
                                                          "source": "AI",
                                                          "itemOrder": 2
                                                        },
                                                        {
                                                          "itemId": 13,
                                                          "content": "아침에 물 한 컵 마시기",
                                                          "category": null,
                                                          "source": "CUSTOM",
                                                          "itemOrder": 3
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "현재 단계에 맞지 않는 개수를 요청했거나 이미 3개가 저장된 경우")
    })
    @PostMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> saveMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "최초 확정 시 3개 객체를, 삭제 후 보충 시 부족한 개수만큼의 객체를 전달합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "최초 고정 미션 3개 확정",
                                            value = """
                                                    {
                                                      "items": [
                                                        {
                                                          "content": "세안 후 3분 안에 보습제를 충분히 발라 수분을 잠그기",
                                                          "category": "MOISTURE",
                                                          "source": "AI"
                                                        },
                                                        {
                                                          "content": "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                          "category": "SUN_PROTECTION",
                                                          "source": "AI"
                                                        },
                                                        {
                                                          "content": "아침에 물 한 컵 마시기",
                                                          "category": null,
                                                          "source": "CUSTOM"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "삭제 후 부족한 1개 보충",
                                            value = """
                                                    {
                                                      "items": [
                                                        {
                                                          "content": "외출 전 선크림을 충분히 바르기",
                                                          "category": "SUN_PROTECTION",
                                                          "source": "AI"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody MorningRoutineSaveRequest request
    ) {
        return ResponseEntity.ok(missionService.saveMorningRoutine(userDetails.getUserId(), request));
    }

    @Operation(
            summary = "오늘 아침 미션 생성 또는 조회",
            operationId = "mission05GenerateMorningMission",
            description = """
                    사용자가 확정한 고정 MorningRoutine 3개를 기반으로 오늘 날짜의 실제 수행용 GeneratedMission을 생성합니다.
                    고정 MorningRoutine과 오늘의 GeneratedMission은 다른 개념입니다.
                    12:00 이후 오늘 아침 미션이 처음 생성되는 경우, 이미 아침 수행 시간이 지났으므로 자동으로 FAILED 상태가 되며 step 완료가 불가능합니다.
                    이 처리는 오늘 날짜의 GeneratedMission(MORNING)에만 적용되며, 고정 MorningRoutine 설정에는 영향을 주지 않습니다.
                    stepIds[0]은 steps[0]에 대응하며, step 완료 API에서는 missionId가 아니라 stepId를 사용합니다.
                    다음 단계에서는 /api/missions/steps/{stepId} 로 각 step을 완료 처리합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘 아침 미션 생성 또는 기존 미션 반환 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "오늘의 아침 미션",
                                              "description": "확정한 고정 아침 미션을 오늘 아침 실천할 수 있도록 생성한 미션입니다.",
                                              "stepIds": [101, 102, 103],
                                              "steps": [
                                                "세안 후 3분 안에 보습제를 충분히 바르기",
                                                "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                "아침에 물 한 컵 마시기"
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/morning")
    public ResponseEntity<MorningMissionResponse> generateMorningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.generateMorningMission(userDetails.getUserId()));
    }

    @Operation(
            summary = "미션 step 완료 처리",
            operationId = "mission06CompleteMissionStep",
            description = """
                    GeneratedMission의 특정 step 완료 상태를 저장합니다.
                    stepId는 오늘 아침 미션 생성 API, 오늘 미션 조회 API, 특정 미션 step 상세 조회 API에서 받은 stepIds 또는 stepId 값을 사용해야 합니다.
                    missionId와 stepId를 혼동하면 안 됩니다.
                    동일 step을 다시 완료해도 포인트가 중복 지급되지 않도록 기존 로직이 유지됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "step 완료 처리 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 stepId"),
            @ApiResponse(responseCode = "400", description = "이미 실패 처리된 아침 미션 step 등 완료할 수 없는 경우")
    })
    @PatchMapping("/steps/{stepId}")
    public ResponseEntity<Void> completeMissionStep(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "완료 처리할 GeneratedMissionStep ID. 오늘 미션 생성/조회 응답의 stepIds 값을 사용합니다.", example = "101")
            @PathVariable Long stepId
    ) {
        missionService.completeStep(userDetails.getUserId(), stepId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "고정 아침 미션 삭제",
            operationId = "mission07DeleteMorningRoutineItem",
            description = """
                    현재 사용자가 매일 수행하도록 저장해 둔 고정 아침 미션 항목 하나를 삭제합니다.
                    삭제 대상은 MorningRoutineItem이며, 과거 GeneratedMission 기록이나 완료 기록은 삭제되지 않습니다.
                    itemId는 고정 아침 미션 조회 API 응답의 itemId 값을 사용합니다.

                    삭제 후 사용자 플로우:
                    1. 원하는 category로 AI 추천 API 호출
                    2. 부족한 개수만큼 추천 받기
                    3. 고정 미션 저장 API로 다시 추가
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "삭제 후 남은 고정 미션 반환",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "routineId": 1,
                                              "items": [
                                                {
                                                  "itemId": 11,
                                                  "content": "세안 후 3분 안에 보습제를 충분히 바르기",
                                                  "category": "수분/보습",
                                                  "source": "AI",
                                                  "itemOrder": 1
                                                },
                                                {
                                                  "itemId": 13,
                                                  "content": "아침에 물 한 컵 마시기",
                                                  "category": null,
                                                  "source": "CUSTOM",
                                                  "itemOrder": 2
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/morning-routine/items/{itemId}")
    public ResponseEntity<MorningRoutineResponse> deleteMorningRoutineItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    description = "삭제할 고정 아침 미션 항목 ID. 고정 아침 미션 조회 API 응답의 itemId 값을 사용합니다.",
                    example = "12"
            )
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(missionService.deleteMorningRoutineItem(userDetails.getUserId(), itemId));
    }

    @Operation(
            summary = "저녁 상태 입력 후 저녁 미션 생성",
            operationId = "mission08GenerateEveningMission",
            description = """
                    오늘 귀가 후 피부/생활 상태를 전달하고 같은 요청에서 저녁 미션 3개를 생성합니다.
                    conditions는 복수 선택 가능하지만 NONE은 단독 선택만 가능합니다.
                    프론트는 noneExclusive 같은 별도 boolean을 보내지 않고 conditions 배열만 보내면 됩니다.
                    AI는 현재 상태, 오늘 아침 미션 수행 여부, 고정 아침 미션, 일정 정보 등을 함께 고려해 저녁 미션을 생성합니다.
                    다음 단계에서는 오늘 미션 조회 또는 step 완료 API를 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "저녁 미션 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "title": "오늘 저녁 회복 미션",
                                              "description": "오늘 저녁 피부와 컨디션을 고려한 회복 루틴입니다.",
                                              "stepIds": [201, 202, 203],
                                              "steps": [
                                                "미온수 세안 후 자극 없는 보습제를 충분히 바르기",
                                                "귀가 후 물 한 컵을 마시고 실내 습도를 점검하기",
                                                "취침 전 10분간 가벼운 스트레칭으로 긴장을 풀기"
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "NONE과 다른 상태를 함께 보낸 경우 등 잘못된 요청")
    })
    @PostMapping("/evening")
    public ResponseEntity<EveningMissionResponse> generateEveningMission(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "현재 저녁 상태를 conditions 배열로 보냅니다. NONE은 단독 선택만 가능합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "복수 선택 예시",
                                            value = """
                                                    {
                                                      "conditions": [
                                                        "RED_HOT",
                                                        "DRY_TIGHT"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "NONE 단독 선택 예시",
                                            value = """
                                                    {
                                                      "conditions": [
                                                        "NONE"
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "잘못된 예시",
                                            value = """
                                                    {
                                                      "conditions": [
                                                        "NONE",
                                                        "RED_HOT"
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody EveningMissionCreateRequest request
    ) {
        return ResponseEntity.ok(missionService.generateEveningMission(userDetails.getUserId(), request.conditions()));
    }

    @Operation(
            summary = "오늘 미션 조회",
            operationId = "mission09GetTodayMissions",
            description = """
                    오늘 생성된 아침 미션과 저녁 미션을 한 번에 조회합니다.
                    stepIds는 step 완료 API에서 사용하는 ID이며, steps 배열과 같은 순서로 대응합니다.
                    아침 또는 저녁 미션이 아직 생성되지 않은 경우 해당 필드는 null일 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘 미션 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "morningMission": {
                                                "title": "오늘의 아침 미션",
                                                "description": "확정한 고정 아침 미션을 오늘 아침 실천할 수 있도록 생성한 미션입니다.",
                                                "stepIds": [101, 102, 103],
                                                "steps": [
                                                  "세안 후 3분 안에 보습제를 충분히 바르기",
                                                  "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                  "아침에 물 한 컵 마시기"
                                                ]
                                              },
                                              "eveningMission": {
                                                "title": "오늘 저녁 회복 미션",
                                                "description": "오늘 저녁 피부와 컨디션을 고려한 회복 루틴입니다.",
                                                "stepIds": [201, 202, 203],
                                                "steps": [
                                                  "미온수 세안 후 자극 없는 보습제를 충분히 바르기",
                                                  "귀가 후 물 한 컵을 마시고 실내 습도를 점검하기",
                                                  "취침 전 10분간 가벼운 스트레칭으로 긴장을 풀기"
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/today")
    public ResponseEntity<TodayMissionResponse> getTodayMissions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getTodayMissions(userDetails.getUserId()));
    }

    @Operation(
            summary = "Mission 옵션 조회",
            operationId = "mission90GetMissionOptions",
            description = """
                    고정 아침 미션 category와 저녁 상태 enum을 한 번에 조회하는 공통 옵션 API입니다.
                    프론트가 category 코드와 저녁 상태 코드를 Swagger 또는 이 응답으로 확인한 뒤,
                    추천 API나 저녁 미션 생성 API에 그대로 사용할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "공통 옵션 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "morningCategories": [
                                                { "code": "MOISTURE", "label": "수분/보습" },
                                                { "code": "SUN_PROTECTION", "label": "자외선 차단" }
                                              ],
                                              "eveningConditions": [
                                                { "code": "RED_HOT", "label": "붉고 뜨거움" },
                                                { "code": "NONE", "label": "해당사항 없음" }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/options")
    public ResponseEntity<MissionOptionsResponse> getMissionOptions() {
        return ResponseEntity.ok(missionService.getMissionOptions());
    }

    @Operation(
            summary = "현재 고정 아침 미션 조회",
            operationId = "mission91GetMorningRoutine",
            description = """
                    현재 사용자가 매일 수행하도록 확정한 고정 아침 미션을 조회합니다.
                    최초 설문 항목은 반환하지 않으며, 실제 MorningRoutineItem만 반환합니다.
                    응답의 itemId는 삭제 API에서 사용합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "고정 아침 미션 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "routineId": 1,
                                              "items": [
                                                {
                                                  "itemId": 11,
                                                  "content": "세안 후 3분 안에 보습제를 충분히 바르기",
                                                  "category": "수분/보습",
                                                  "source": "AI",
                                                  "itemOrder": 1
                                                },
                                                {
                                                  "itemId": 12,
                                                  "content": "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                  "category": "자외선 차단",
                                                  "source": "AI",
                                                  "itemOrder": 2
                                                },
                                                {
                                                  "itemId": 13,
                                                  "content": "아침에 물 한 컵 마시기",
                                                  "category": null,
                                                  "source": "CUSTOM",
                                                  "itemOrder": 3
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/morning-routine")
    public ResponseEntity<MorningRoutineResponse> getMorningRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(missionService.getMorningRoutine(userDetails.getUserId()));
    }

    @Operation(
            summary = "특정 미션 step 상세 조회",
            operationId = "mission92GetMissionSteps",
            description = """
                    특정 GeneratedMission의 stepId, stepOrder, 완료 여부를 상세 조회합니다.
                    missionId는 날짜별 미션 조회 API 응답의 missionId 값을 사용합니다.
                    이 응답의 stepId를 step 완료 API에 사용할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "step 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "stepId": 101,
                                                "stepOrder": 1,
                                                "content": "세안 후 3분 안에 보습제를 충분히 바르기",
                                                "completed": false
                                              },
                                              {
                                                "stepId": 102,
                                                "stepOrder": 2,
                                                "content": "외출 전 자외선 차단제를 꼼꼼하게 바르기",
                                                "completed": true
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{missionId}/steps")
    public ResponseEntity<List<MissionStepDetailResponse>> getMissionSteps(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 GeneratedMission ID. 날짜별 미션 조회 응답의 missionId 값을 사용합니다.", example = "55")
            @PathVariable Long missionId
    ) {
        return ResponseEntity.ok(missionService.getMissionSteps(userDetails.getUserId(), missionId));
    }

    @Operation(
            summary = "날짜별 미션 조회",
            operationId = "mission93GetMissionsByDate",
            description = """
                    특정 날짜에 생성된 미션 목록을 조회합니다.
                    응답의 missionId는 특정 미션 step 상세 조회 API에서 사용합니다.
                    이 API는 날짜별 기록 조회용이며, step 완료에는 missionId가 아니라 stepId가 필요합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "날짜별 미션 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "missionId": 55,
                                                "missionTime": "MORNING",
                                                "title": "오늘의 아침 미션",
                                                "completed": false,
                                                "steps": [
                                                  {
                                                    "stepId": 101,
                                                    "stepOrder": 1,
                                                    "content": "세안 후 3분 안에 보습제를 충분히 바르기",
                                                    "completed": false
                                                  }
                                                ]
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/date")
    public ResponseEntity<List<MissionByDateResponse>> getMissionsByDate(
            @Parameter(description = "조회할 사용자 ID", example = "1")
            @RequestParam Long userId,
            @Parameter(description = "조회할 날짜. yyyy-MM-dd 형식", example = "2026-08-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getMissionsByDate(userId, date));
    }
    @Operation(
            summary = "주간 미션 완료 현황 조회",
            operationId = "mission94GetWeeklyMissionStatus",
            description = """
                    기준 날짜가 포함된 주의 월요일부터 일요일까지 주간 미션 완료 현황을 조회합니다.
                    아침(MORNING)과 저녁(EVENING) 미션이 모두 COMPLETED인 날만 completed=true 입니다.
                    둘 중 하나라도 미완료, FAILED, 미생성이면 completed=false 입니다.
                    미래 날짜는 항상 completed=false 입니다.
                    응답 days는 항상 월요일부터 일요일까지 7개를 반환합니다.
                    이 API는 조회 전용이며 GeneratedMission을 생성하지 않습니다.
                    프론트 주간 체크 UI에서 completed=true면 체크 표시, false면 빈 원으로 사용할 수 있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주간 미션 완료 현황 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WeeklyMissionStatusResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "startDate": "2026-07-27",
                                              "endDate": "2026-08-02",
                                              "days": [
                                                {
                                                  "date": "2026-07-27",
                                                  "completed": true
                                                },
                                                {
                                                  "date": "2026-07-28",
                                                  "completed": true
                                                },
                                                {
                                                  "date": "2026-07-29",
                                                  "completed": true
                                                },
                                                {
                                                  "date": "2026-07-30",
                                                  "completed": false
                                                },
                                                {
                                                  "date": "2026-07-31",
                                                  "completed": false
                                                },
                                                {
                                                  "date": "2026-08-01",
                                                  "completed": false
                                                },
                                                {
                                                  "date": "2026-08-02",
                                                  "completed": false
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/week")
    public ResponseEntity<WeeklyMissionStatusResponse> getWeeklyMissionStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(
                    description = "기준 날짜. 이 날짜가 포함된 주의 월요일부터 일요일까지 조회합니다.",
                    example = "2026-07-30",
                    schema = @Schema(type = "string", format = "date")
            )
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(missionService.getWeeklyMissionStatus(userDetails.getUserId(), date));
    }
}
