package com.likelion.staycare.domain.mission.service;

import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.Diagnosis;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.diagnosis.repository.DiagnosisRepository;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionStepAddRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSaveRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSurveySaveRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionOptionItemResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepCompleteResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionOptionsResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineItemResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineSurveyOptionsResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.WeeklyMissionDayResponse;
import com.likelion.staycare.domain.mission.dto.response.WeeklyMissionStatusResponse;
import com.likelion.staycare.domain.mission.entity.DailySkinCheck;
import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.MorningRoutine;
import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
import com.likelion.staycare.domain.mission.entity.enums.EveningCondition;
import com.likelion.staycare.domain.mission.entity.enums.MissionStatus;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import com.likelion.staycare.domain.mission.entity.enums.MorningMissionCategory;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineSurveyOption;
import com.likelion.staycare.domain.mission.entity.enums.SkinCondition;
import com.likelion.staycare.domain.mission.exception.MissionErrorCode;
import com.likelion.staycare.domain.mission.repository.DailySkinCheckRepository;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionRepository;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionStepRepository;
import com.likelion.staycare.domain.mission.repository.MorningRoutineItemRepository;
import com.likelion.staycare.domain.mission.repository.MorningRoutineRepository;
import com.likelion.staycare.domain.mission.repository.UserMissionStepCheckRepository;
import com.likelion.staycare.domain.point.service.PointService;
import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.schedule.entity.enums.Companion;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
import com.likelion.staycare.domain.schedule.entity.enums.ScheduleStatus;
import com.likelion.staycare.domain.schedule.repository.ScheduleRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private static final String DEFAULT_VALUE = "해당없음";
    private static final String EMPTY_TIP = "";
    private static final String MORNING_TITLE = "오늘의 아침 미션";
    private static final String MORNING_DESCRIPTION = "확정한 고정 아침 미션을 오늘 아침 실천할 수 있도록 생성한 미션입니다.";
    private static final String EVENING_FALLBACK_TITLE = "오늘 저녁 회복 미션";
    private static final String PROMPT_VERSION = "v2";
    private static final int MORNING_ROUTINE_SIZE = 3;
    private static final int MORNING_RECOMMENDATION_COUNT = 3;
    private static final int EVENING_CATEGORY_MAX_COUNT = 2;
    private static final int EVENING_EDITABLE_STEP_SIZE = 3;
    private static final int MORNING_ROUTINE_TEMP_ORDER_OFFSET = 1000;
    private static final int EVENING_STEP_TEMP_ORDER_OFFSET = 2000;
    private static final LocalTime MORNING_END_TIME = LocalTime.NOON;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final DailySkinCheckRepository dailySkinCheckRepository;
    private final GeneratedMissionRepository generatedMissionRepository;
    private final GeneratedMissionStepRepository generatedMissionStepRepository;
    private final UserMissionStepCheckRepository userMissionStepCheckRepository;
    private final MorningRoutineRepository morningRoutineRepository;
    private final MorningRoutineItemRepository morningRoutineItemRepository;
    private final ScheduleRepository scheduleRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final OpenAiService openAiService;
    private final PointService pointService;

    private String buildScheduleStep(Schedule schedule) {
        if (schedule.getCategory() == ScheduleCategory.WEDDING) {
            return getCompanionSubjectLabel(schedule.getCompanion()) + " 결혼식 참석하기";
        }

        return getCompanionActionLabel(schedule.getCompanion()) + " "
                + getScheduleCategoryActionLabel(schedule.getCategory());
    }



    public MissionOptionsResponse getMissionOptions() {
        return MissionOptionsResponse.builder()
                .morningCategories(toOptionItems(List.of(MorningMissionCategory.values())))
                .eveningConditions(toOptionItems(List.of(EveningCondition.values())))
                .build();
    }

    public MorningRoutineSurveyOptionsResponse getMorningRoutineSurveyOptions() {
        return MorningRoutineSurveyOptionsResponse.builder()
                .items(toOptionItems(List.of(MorningRoutineSurveyOption.values())))
                .maxSelections(MORNING_ROUTINE_SIZE)
                .build();
    }

    @Transactional
    public void saveMorningRoutineSurvey(Long userId, MorningRoutineSurveySaveRequest request) {
        getUser(userId);
    }

    public MorningRoutineRecommendationResponse recommendMorningRoutine(
            Long userId,
            MorningRoutineRecommendationRequest request
    ) {
        validateMorningCategoryCount(request.categories());

        User user = getUser(userId);
        List<String> currentFixedMissions = morningRoutineRepository.findByUserId(userId)
                .map(this::getActualMorningRoutineItems)
                .orElse(List.of())
                .stream()
                .map(MorningRoutineItem::getContent)
                .toList();
        int recommendationCount = MORNING_ROUTINE_SIZE - currentFixedMissions.size();
        if (recommendationCount <= 0) {
            throw new CustomException(MissionErrorCode.MORNING_ROUTINE_ALREADY_FULL);
        }

        List<String> recommendations = openAiService.recommendMorningRoutine(
                getAge(user.getAge()),
                getGender(user),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getCareMotivation(user),
                getLatestDiagnosisRecommendation(userId),
                request.categories(),
                currentFixedMissions,
                Math.min(MORNING_RECOMMENDATION_COUNT, recommendationCount)
        );

        return MorningRoutineRecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    @Transactional
    public MorningRoutineResponse saveMorningRoutine(Long userId, MorningRoutineSaveRequest request) {
        if (request.items() == null || request.items().isEmpty() || request.items().size() > MORNING_ROUTINE_SIZE) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }

        User user = getUser(userId);
        MorningRoutine routine = getOrCreateRoutine(user);
        normalizeMorningRoutineItemOrder(routine);
        List<MorningRoutineItem> existingItems = getActualMorningRoutineItems(routine);
        int existingCount = existingItems.size();

        if (existingCount == MORNING_ROUTINE_SIZE) {
            throw new CustomException(MissionErrorCode.MORNING_ROUTINE_ALREADY_FULL);
        }
        if (existingCount == 0 && request.items().size() != MORNING_ROUTINE_SIZE) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }
        if (existingCount > 0 && request.items().size() != MORNING_ROUTINE_SIZE - existingCount) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }

        List<MorningRoutineItem> items = new ArrayList<>();
        for (int index = 0; index < request.items().size(); index++) {
            var itemRequest = request.items().get(index);
            items.add(MorningRoutineItem.builder()
                    .morningRoutine(routine)
                    .content(itemRequest.content())
                    .category(itemRequest.category() == null ? null : itemRequest.category().getLabel())
                    .source(itemRequest.source())
                    .itemOrder(existingCount + index + 1)
                    .build());
        }

        morningRoutineItemRepository.saveAllAndFlush(items);
        normalizeMorningRoutineItemOrder(routine);
        return toMorningRoutineResponse(routine, getActualMorningRoutineItems(routine));
    }

    public MorningRoutineResponse getMorningRoutine(Long userId) {
        MorningRoutine routine = morningRoutineRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND));

        List<MorningRoutineItem> actualItems = getActualMorningRoutineItems(routine);
        if (actualItems.isEmpty()) {
            throw new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND);
        }

        return toMorningRoutineResponse(routine, actualItems);
    }

    @Transactional
    public MorningRoutineResponse deleteMorningRoutineItem(Long userId, Long itemId) {
        MorningRoutineItem item = morningRoutineItemRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MORNING_ROUTINE_ITEM_NOT_FOUND));

        if (!item.getMorningRoutine().getUser().getId().equals(userId)) {
            throw new CustomException(MissionErrorCode.FORBIDDEN_MISSION);
        }

        MorningRoutine routine = item.getMorningRoutine();
        morningRoutineItemRepository.delete(item);
        morningRoutineItemRepository.flush();
        normalizeMorningRoutineItemOrder(routine);
        return toMorningRoutineResponse(routine, getActualMorningRoutineItems(routine));
    }

    @Transactional
    public MorningMissionResponse generateMorningMission(Long userId) {
        User user = getUser(userId);
        GeneratedMission mission = getOrCreateMorningMission(user, getCurrentDate(), true);
        syncScheduleDerivedStep(user.getId(), mission);
        return toMorningMissionResponse(mission);
    }

    @Transactional
    public EveningMissionResponse generateEveningMission(Long userId, Set<EveningCondition> conditions) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission existingMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElse(null);

        if (existingMission != null) {
            syncScheduleDerivedStep(userId, existingMission);
            return toEveningMissionResponse(existingMission);
        }

        GeneratedMission morningMission = getOrCreateMorningMission(user, today, true);

        DailySkinCheck dailySkinCheck = dailySkinCheckRepository.findByUserAndCheckedDate(user, today)
                .map(saved -> {
                    saved.updateSkinCondition(resolveRepresentativeSkinCondition(conditions));
                    return saved;
                })
                .orElseGet(() -> dailySkinCheckRepository.save(
                        DailySkinCheck.builder()
                                .user(user)
                                .skinCondition(resolveRepresentativeSkinCondition(conditions))
                                .checkedDate(today)
                                .build()
                ));

        EveningMissionRequest request = new EveningMissionRequest(
                getAge(user.getAge()),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getTodayScheduleContext(userId, today),
                joinEveningConditions(conditions),
                buildMorningMissionStatus(morningMission)
        );

        EveningMissionResponse response = openAiService.generateEveningMission(request);
        GeneratedMission savedMission = saveGeneratedMission(
                user,
                dailySkinCheck,
                response.title() == null || response.title().isBlank() ? EVENING_FALLBACK_TITLE : response.title(),
                response.description(),
                today,
                MissionTime.EVENING
        );

        List<String> steps = new ArrayList<>(response.steps());
        scheduleRepository.findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        today,
                        today,
                        ScheduleStatus.ACTIVE
                )
                .map(this::buildScheduleStep)
                .ifPresent(steps::add);



        List<GeneratedMissionStep> savedSteps = saveGeneratedMissionSteps(savedMission, steps);
        return EveningMissionResponse.builder()
                .description(savedMission.getDescription())
                .stepIds(extractStepIds(savedSteps))
                .steps(steps)
                .build();
    }

    public EveningMissionResponse getEveningMission(Long userId) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission eveningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));

        syncScheduleDerivedStep(userId, eveningMission);
        return toEveningMissionResponse(eveningMission);
    }

    public EveningMissionRecommendationResponse recommendEveningMission(
            Long userId,
            EveningMissionRecommendationRequest request
    ) {
        validateEveningCategoryCount(request.categories());

        User user = getUser(userId);
        LocalDate today = getCurrentDate();
        GeneratedMission eveningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));
        validateEveningMissionEditable(eveningMission);
        GeneratedMission morningMission = getOrCreateMorningMission(user, today, true);

        List<GeneratedMissionStep> editableEveningSteps = getEditableEveningSteps(eveningMission);
        int remainingSlots = EVENING_EDITABLE_STEP_SIZE - editableEveningSteps.size();
        if (remainingSlots <= 0) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_ALREADY_FULL);
        }

        List<String> recommendations = openAiService.recommendEveningMissions(
                getAge(user.getAge()),
                getGender(user),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getCareMotivation(user),
                getLatestDiagnosisRecommendation(userId),
                getTodayScheduleContext(userId, today),
                getEveningRecommendationConditionText(eveningMission),
                buildMorningMissionStatus(morningMission),
                request.categories(),
                editableEveningSteps.stream().map(GeneratedMissionStep::getContent).toList(),
                remainingSlots
        );

        return EveningMissionRecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    @Transactional
    public EveningMissionResponse deleteEveningMissionStep(Long userId, Long stepId) {
        User user = getUser(userId);
        GeneratedMissionStep step = generatedMissionStepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_STEP_NOT_FOUND));

        GeneratedMission mission = step.getGeneratedMission();
        validateMissionOwner(user, mission);
        validateEveningMissionEditable(mission);
        validateEditableEveningStep(step);

        UserMissionStepCheck stepCheck = userMissionStepCheckRepository.findByGeneratedMissionStepId(stepId).orElse(null);
        if (stepCheck != null && stepCheck.isChecked()) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_STEP_EDIT_NOT_ALLOWED);
        }
        if (stepCheck != null) {
            userMissionStepCheckRepository.deleteByGeneratedMissionStepId(stepId);
            userMissionStepCheckRepository.flush();
        }

        generatedMissionStepRepository.delete(step);
        generatedMissionStepRepository.flush();
        normalizeEveningEditableStepOrder(mission);
        return toEveningMissionResponse(mission);
    }

    @Transactional
    public EveningMissionResponse addEveningMissionSteps(Long userId, EveningMissionStepAddRequest request) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();
        GeneratedMission mission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));

        validateEveningMissionEditable(mission);
        normalizeEveningEditableStepOrder(mission);
        List<GeneratedMissionStep> editableSteps = getEditableEveningSteps(mission);
        int remainingSlots = EVENING_EDITABLE_STEP_SIZE - editableSteps.size();
        if (remainingSlots <= 0) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_ALREADY_FULL);
        }
        if (request.steps() == null || request.steps().isEmpty() || request.steps().size() > remainingSlots) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_ADD_SIZE_INVALID);
        }

        int nextOrder = editableSteps.size() + 1;
        List<GeneratedMissionStep> newSteps = new ArrayList<>();
        for (String content : request.steps()) {
            newSteps.add(GeneratedMissionStep.builder()
                    .generatedMission(mission)
                    .content(content)
                    .stepOrder(nextOrder++)
                    .build());
        }
        generatedMissionStepRepository.saveAllAndFlush(newSteps);
        return toEveningMissionResponse(mission);
    }

    @Transactional
    public TodayMissionResponse getTodayMissions(Long userId) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission morningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.MORNING)
                .orElse(null);

        if (morningMission == null && isAfterMorningWindow()) {
            morningMission = getOrCreateMorningMission(user, today, true);
        }
        if (morningMission != null) {
            syncScheduleDerivedStep(userId, morningMission);
        }

        GeneratedMission eveningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElse(null);
        if (eveningMission != null) {
            syncScheduleDerivedStep(userId, eveningMission);
        }

        return TodayMissionResponse.builder()
                .morningMission(morningMission == null ? null : toMorningMissionResponse(morningMission))
                .eveningMission(eveningMission == null ? null : toEveningMissionResponse(eveningMission))
                .build();
    }

    public List<MissionStepDetailResponse> getMissionSteps(Long userId, Long missionId) {
        User user = getUser(userId);
        GeneratedMission mission = getMission(missionId);
        validateMissionOwner(user, mission);
        return buildStepDetails(mission);
    }

    public List<MissionByDateResponse> getMissionsByDate(Long userId, LocalDate date) {
        User user = getUser(userId);
        return generatedMissionRepository.findAllByUserAndMissionDateOrderByMissionTimeAsc(user, date)
                .stream()
                .map(this::toMissionByDateResponse)
                .toList();
    }

    public WeeklyMissionStatusResponse getWeeklyMissionStatus(Long userId, LocalDate date) {
        User user = getUser(userId);
        LocalDate weekStart = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);
        LocalDate today = getCurrentDate();

        Map<LocalDate, Set<MissionTime>> completedMissionTimesByDate = generatedMissionRepository
                .findAllByUserAndMissionDateBetweenOrderByMissionDateAscMissionTimeAsc(user, weekStart, weekEnd)
                .stream()
                .filter(mission -> mission.getStatus() == MissionStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        GeneratedMission::getMissionDate,
                        Collectors.mapping(GeneratedMission::getMissionTime, Collectors.toSet())
                ));

        List<WeeklyMissionDayResponse> days = weekStart.datesUntil(weekEnd.plusDays(1))
                .map(currentDate -> WeeklyMissionDayResponse.builder()
                        .date(currentDate)
                        .completed(isCompletedDay(currentDate, today, completedMissionTimesByDate))
                        .build())
                .toList();

        return WeeklyMissionStatusResponse.builder()
                .startDate(weekStart)
                .endDate(weekEnd)
                .days(days)
                .build();
    }

    @Transactional
    public MissionStepCompleteResponse completeStep(Long userId, Long stepId) {
        try {
            User user = getUser(userId);
            GeneratedMissionStep generatedMissionStep = generatedMissionStepRepository.findById(stepId)
                    .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_STEP_NOT_FOUND));

            GeneratedMission mission = generatedMissionStep.getGeneratedMission();
            validateMissionOwner(user, mission);
            validateStepCompletionAllowed(mission);

            UserMissionStepCheck stepCheck = userMissionStepCheckRepository.findByGeneratedMissionStepId(stepId)
                    .orElseGet(() -> UserMissionStepCheck.builder()
                            .generatedMissionStep(generatedMissionStep)
                            .build());

            boolean alreadyChecked = stepCheck.isChecked();
            stepCheck.updateChecked(true);
            userMissionStepCheckRepository.saveAndFlush(stepCheck);
            int stepRewardPoint = 0;
            int dailyBonusPoint = 0;
            boolean missionCompleted = false;
            boolean dailyMissionsCompleted = false;

            // 1) step 1개 완료 = 1포인트
            if (!alreadyChecked) {
                stepRewardPoint = pointService.rewardMissionStep(user, generatedMissionStep);
            }

            // 2) 현재 mission(아침 또는 저녁)의 모든 step 완료 여부
            if (isAllStepsCompleted(mission)) {
                if (mission.getStatus() != MissionStatus.COMPLETED) {
                    mission.complete(LocalDateTime.now(KOREA_ZONE_ID));
                    generatedMissionRepository.saveAndFlush(mission);
                }
                missionCompleted = true;

                // 3) 아침 + 저녁 mission 둘 다 완료면 하루 완료 보너스 +2
                if (isAllDailyMissionsCompleted(user, mission.getMissionDate())) {
                    dailyMissionsCompleted = true;
                    dailyBonusPoint = pointService.rewardDailyMissionComplete(user, mission.getMissionDate());
                }
            }

            // 4) 필요하면 여기서 stamp 일별 집계 갱신 호출
            // stampService.refreshDailyStamp(user.getId(), mission.getMissionDate());
            int totalPoint = pointService.getMyPoint(user.getId()).getPoint();
            return MissionStepCompleteResponse.builder()
                    .stepRewardPoint(stepRewardPoint)
                    .dailyBonusPoint(dailyBonusPoint)
                    .awardedPoint(stepRewardPoint + dailyBonusPoint)
                    .totalPoint(totalPoint)
                    .missionCompleted(missionCompleted)
                    .dailyMissionsCompleted(dailyMissionsCompleted)
                    .build();

        } catch (CustomException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new CustomException(MissionErrorCode.MISSION_STEP_PROCESS_FAILED);
        }
    }


    private boolean isAllDailyMissionsCompleted(User user, LocalDate date) {
        List<GeneratedMission> missions = generatedMissionRepository
                .findAllByUserAndMissionDateOrderByMissionTimeAsc(user, date);

        boolean morningCompleted = missions.stream()
                .anyMatch(mission -> mission.getMissionTime() == MissionTime.MORNING
                        && mission.getStatus() == MissionStatus.COMPLETED);

        boolean eveningCompleted = missions.stream()
                .anyMatch(mission -> mission.getMissionTime() == MissionTime.EVENING
                        && mission.getStatus() == MissionStatus.COMPLETED);

        return morningCompleted && eveningCompleted;
    }




    private List<MissionOptionItemResponse> toOptionItems(Collection<? extends Enum<?>> enums) {
        return enums.stream()
                .map(value -> MissionOptionItemResponse.builder()
                        .code(value.name())
                        .label(extractLabel(value))
                        .build())
                .toList();
    }

    private String extractLabel(Enum<?> value) {
        if (value instanceof MorningMissionCategory category) {
            return category.getLabel();
        }
        if (value instanceof EveningCondition condition) {
            return condition.getLabel();
        }
        if (value instanceof MorningRoutineSurveyOption option) {
            return option.getLabel();
        }
        return value.name();
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
    }

    private MorningRoutine getOrCreateRoutine(User user) {
        return morningRoutineRepository.findByUserId(user.getId())
                .orElseGet(() -> morningRoutineRepository.save(MorningRoutine.builder().user(user).build()));
    }

    private GeneratedMission getMission(Long missionId) {
        return generatedMissionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_NOT_FOUND));
    }

    private void validateMissionOwner(User user, GeneratedMission mission) {
        if (!mission.getUser().getId().equals(user.getId())) {
            throw new CustomException(MissionErrorCode.FORBIDDEN_MISSION);
        }
    }

    private void validateMorningCategoryCount(List<MorningMissionCategory> categories) {
        if (categories != null && categories.size() > MORNING_ROUTINE_SIZE) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }
    }

    private void validateEveningCategoryCount(List<MorningMissionCategory> categories) {
        if (categories != null && categories.size() > EVENING_CATEGORY_MAX_COUNT) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }
    }

    private void validateEveningMissionEditable(GeneratedMission mission) {
        if (mission.getMissionTime() != MissionTime.EVENING || mission.getStatus() == MissionStatus.COMPLETED) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_STEP_EDIT_NOT_ALLOWED);
        }
    }

    private void validateEditableEveningStep(GeneratedMissionStep step) {
        if (step.getGeneratedMission().getMissionTime() != MissionTime.EVENING
                || step.getStepOrder() > EVENING_EDITABLE_STEP_SIZE) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_STEP_EDIT_NOT_ALLOWED);
        }
    }

    private GeneratedMission getOrCreateMorningMission(User user, LocalDate missionDate, boolean allowAutoMissed) {
        GeneratedMission existingMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, missionDate, MissionTime.MORNING)
                .orElse(null);
        if (existingMission != null) {
            return existingMission;
        }

        MorningRoutine routine = morningRoutineRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND));

        List<String> steps = getActualMorningRoutineItems(routine).stream()
                .map(MorningRoutineItem::getContent)
                .collect(Collectors.toCollection(ArrayList::new));

        if (steps.isEmpty()) {
            throw new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND);
        }

        scheduleRepository.findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        user.getId(),
                        missionDate,
                        missionDate,
                        ScheduleStatus.ACTIVE
                )
                .map(this::buildScheduleStep)
                .ifPresent(steps::add);

        GeneratedMission savedMission = saveGeneratedMission(
                user,
                null,
                MORNING_TITLE,
                MORNING_DESCRIPTION,
                missionDate,
                MissionTime.MORNING
        );
        saveGeneratedMissionSteps(savedMission, steps);

        return savedMission;
    }

    private GeneratedMission saveGeneratedMission(
            User user,
            DailySkinCheck dailySkinCheck,
            String title,
            String description,
            LocalDate missionDate,
            MissionTime missionTime
    ) {
        return generatedMissionRepository.save(
                GeneratedMission.builder()
                        .user(user)
                        .dailySkinCheck(dailySkinCheck)
                        .title(title)
                        .description(description)
                        .tip(EMPTY_TIP)
                        .promptVersion(PROMPT_VERSION)
                        .missionDate(missionDate)
                        .missionTime(missionTime)
                        .build()
        );
    }

    private List<GeneratedMissionStep> saveGeneratedMissionSteps(GeneratedMission mission, List<String> steps) {
        List<GeneratedMissionStep> generatedMissionSteps = new ArrayList<>();
        for (int index = 0; index < steps.size(); index++) {
            generatedMissionSteps.add(
                    GeneratedMissionStep.builder()
                            .generatedMission(mission)
                            .content(steps.get(index))
                            .stepOrder(index + 1)
                            .build()
            );
        }
        return generatedMissionStepRepository.saveAll(generatedMissionSteps);
    }

    private void syncScheduleDerivedStep(Long userId, GeneratedMission mission) {
        List<GeneratedMissionStep> extraSteps = generatedMissionStepRepository
                .findByGeneratedMissionAndStepOrderGreaterThanOrderByStepOrderAsc(mission, MORNING_ROUTINE_SIZE);

        String scheduleStep = scheduleRepository
                .findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        mission.getMissionDate(),
                        mission.getMissionDate(),
                        ScheduleStatus.ACTIVE
                )
                .map(this::buildScheduleStep)
                .orElse(null);

        if (scheduleStep == null) {
            if (!extraSteps.isEmpty()) {
                userMissionStepCheckRepository.deleteAllByGeneratedMissionStepIn(extraSteps);
                generatedMissionStepRepository.deleteAllInBatch(extraSteps);
            }
            return;
        }

        if (extraSteps.isEmpty()) {
            generatedMissionStepRepository.save(
                    GeneratedMissionStep.builder()
                            .generatedMission(mission)
                            .content(scheduleStep)
                            .stepOrder(MORNING_ROUTINE_SIZE + 1)
                            .build()
            );
            return;
        }

        GeneratedMissionStep scheduleDerivedStep = extraSteps.get(0);
        scheduleDerivedStep.updateContent(scheduleStep);

        if (extraSteps.size() > 1) {
            List<GeneratedMissionStep> redundantSteps = extraSteps.subList(1, extraSteps.size());
            userMissionStepCheckRepository.deleteAllByGeneratedMissionStepIn(redundantSteps);
            generatedMissionStepRepository.deleteAllInBatch(redundantSteps);
        }
    }

    private void validateStepCompletionAllowed(GeneratedMission mission) {
    }

    private LocalDate getCurrentDate() {
        return LocalDate.now(KOREA_ZONE_ID);
    }

    private LocalTime getCurrentTime() {
        return LocalTime.now(KOREA_ZONE_ID);
    }

    private boolean isAfterMorningWindow() {
        return !getCurrentTime().isBefore(MORNING_END_TIME);
    }

    private boolean isAllStepsCompleted(GeneratedMission mission) {
        List<GeneratedMissionStep> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        List<UserMissionStepCheck> checks = userMissionStepCheckRepository.findByGeneratedMissionStepGeneratedMission(mission);
        if (steps.isEmpty() || steps.size() != checks.size()) {
            return false;
        }
        return checks.stream().allMatch(UserMissionStepCheck::isChecked);
    }

    private boolean isCompletedDay(
            LocalDate date,
            LocalDate today,
            Map<LocalDate, Set<MissionTime>> completedMissionTimesByDate
    ) {
        if (date.isAfter(today)) {
            return false;
        }

        Set<MissionTime> completedMissionTimes = completedMissionTimesByDate.get(date);
        return completedMissionTimes != null
                && completedMissionTimes.contains(MissionTime.MORNING)
                && completedMissionTimes.contains(MissionTime.EVENING);
    }

    private MorningMissionResponse toMorningMissionResponse(GeneratedMission mission) {
        List<GeneratedMissionStep> missionSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        return MorningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .stepIds(extractStepIds(missionSteps))
                .steps(missionSteps.stream().map(GeneratedMissionStep::getContent).toList())
                .build();
    }

    private EveningMissionResponse toEveningMissionResponse(GeneratedMission mission) {
        List<GeneratedMissionStep> missionSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        return EveningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .stepIds(extractStepIds(missionSteps))
                .steps(missionSteps.stream().map(GeneratedMissionStep::getContent).toList())
                .build();
    }

    private List<GeneratedMissionStep> getEditableEveningSteps(GeneratedMission mission) {
        return generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission)
                .stream()
                .filter(step -> step.getStepOrder() <= EVENING_EDITABLE_STEP_SIZE)
                .toList();
    }

    private void normalizeEveningEditableStepOrder(GeneratedMission mission) {
        List<GeneratedMissionStep> editableSteps = getEditableEveningSteps(mission);
        if (editableSteps.isEmpty()) {
            return;
        }

        for (int index = 0; index < editableSteps.size(); index++) {
            editableSteps.get(index).updateStepOrder(EVENING_STEP_TEMP_ORDER_OFFSET + index + 1);
        }
        generatedMissionStepRepository.saveAllAndFlush(editableSteps);

        for (int index = 0; index < editableSteps.size(); index++) {
            editableSteps.get(index).updateStepOrder(index + 1);
        }
        generatedMissionStepRepository.saveAllAndFlush(editableSteps);
    }

    private String getEveningRecommendationConditionText(GeneratedMission eveningMission) {
        DailySkinCheck dailySkinCheck = eveningMission.getDailySkinCheck();
        if (dailySkinCheck == null || dailySkinCheck.getSkinCondition() == null) {
            return DEFAULT_VALUE;
        }

        return switch (dailySkinCheck.getSkinCondition()) {
            case NORMAL -> "특이사항 없음";
            case HOT_REDNESS -> "붉고 화끈거림";
            case OILY_STUFFY -> "답답하고 번들거림";
            case TROUBLE_REDNESS -> "트러블과 붉은기";
            case MAKEUP_BREAKDOWN -> "메이크업 잔여감 또는 번들거림";
            case DRY_TIGHT -> "건조하고 당김";
            case OUTDOOR_SENSITIVE -> "외부 환경으로 예민해짐";
        };
    }

    private MissionByDateResponse toMissionByDateResponse(GeneratedMission mission) {
        return MissionByDateResponse.builder()
                .missionId(mission.getId())
                .missionTime(mission.getMissionTime())
                .title(mission.getTitle())
                .completed(mission.getStatus() == MissionStatus.COMPLETED)
                .steps(buildStepDetails(mission))
                .build();
    }

    private List<MissionStepDetailResponse> buildStepDetails(GeneratedMission mission) {
        List<GeneratedMissionStep> missionSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        Map<Long, UserMissionStepCheck> checksByStepId = userMissionStepCheckRepository
                .findByGeneratedMissionStepGeneratedMission(mission)
                .stream()
                .collect(Collectors.toMap(check -> check.getGeneratedMissionStep().getId(), Function.identity()));

        return missionSteps.stream()
                .map(step -> MissionStepDetailResponse.builder()
                        .stepId(step.getId())
                        .stepOrder(step.getStepOrder())
                        .content(step.getContent())
                        .completed(checksByStepId.get(step.getId()) != null && checksByStepId.get(step.getId()).isChecked())
                        .build())
                .toList();
    }

    private String buildMorningMissionStatus(GeneratedMission morningMission) {
        List<GeneratedMissionStep> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(morningMission);
        Map<Long, UserMissionStepCheck> checksByStepId = userMissionStepCheckRepository
                .findByGeneratedMissionStepGeneratedMission(morningMission)
                .stream()
                .collect(Collectors.toMap(check -> check.getGeneratedMissionStep().getId(), Function.identity()));

        return steps.stream()
                .map(step -> step.getStepOrder() + ". " + step.getContent() + " - "
                        + resolveMorningStepState(morningMission, step, checksByStepId))
                .collect(Collectors.joining(", "));
    }

    private String resolveMorningStepState(
            GeneratedMission mission,
            GeneratedMissionStep step,
            Map<Long, UserMissionStepCheck> checksByStepId
    ) {
        UserMissionStepCheck check = checksByStepId.get(step.getId());
        if (check != null && check.isChecked()) {
            return "완료";
        }
        if (mission.getStatus() == MissionStatus.FAILED) {
            return "수행 시간 종료로 완료 불가";
        }
        return "미완료";
    }

    private String getTodayScheduleContext(Long userId, LocalDate missionDate) {
        return scheduleRepository.findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                        userId,
                        missionDate,
                        missionDate,
                        ScheduleStatus.ACTIVE
                )
                .map(this::buildScheduleContext)
                .orElse(DEFAULT_VALUE);
    }

    private String buildScheduleContext(Schedule schedule) {
        return "동행자: " + getCompanionContextLabel(schedule.getCompanion())
                + ", 일정 카테고리: " + getScheduleCategoryContextLabel(schedule.getCategory())
                + ", 시작일: " + schedule.getStartDate()
                + ", 종료일: " + schedule.getEndDate();
    }

    private String getCompanionContextLabel(Companion companion) {
        if (companion == null) {
            return DEFAULT_VALUE;
        }

        return switch (companion) {
            case ALONE -> "혼자";
            case FAMILY -> "가족";
            case FRIEND -> "친구";
            case LOVER -> "연인";
            case COWORKER -> "직장동료";
            case ACQUAINTANCE -> "지인";
        };
    }

    private String getCompanionSubjectLabel(Companion companion) {
        if (companion == null) {
            return "일정";
        }

        return switch (companion) {
            case ALONE -> "혼자";
            case FAMILY -> "가족";
            case FRIEND -> "친구";
            case LOVER -> "연인";
            case COWORKER -> "직장동료";
            case ACQUAINTANCE -> "지인";
        };
    }

    private String getCompanionActionLabel(Companion companion) {
        if (companion == null) {
            return "일정";
        }

        return switch (companion) {
            case ALONE -> "혼자";
            case FAMILY -> "가족과";
            case FRIEND -> "친구와";
            case LOVER -> "연인과";
            case COWORKER -> "직장동료와";
            case ACQUAINTANCE -> "지인과";
        };
    }

    private String getScheduleCategoryContextLabel(ScheduleCategory category) {
        if (category == null) {
            return "일정";
        }

        return switch (category) {
            case SELF_CARE -> "자기관리";
            case MEETING -> "미팅";
            case DATE -> "데이트";
            case TRAVEL -> "여행";
            case EVENT -> "이벤트";
            case CEREMONY -> "경조사";
            case WEDDING -> "결혼식";
            case DRINKING -> "술자리 모임";
            case TALK -> "친목·수다";
        };
    }

    private String getScheduleCategoryActionLabel(ScheduleCategory category) {
        if (category == null) {
            return "일정 소화하기";
        }

        return switch (category) {
            case SELF_CARE -> "자기관리 일정 실천하기";
            case MEETING -> "미팅 일정 소화하기";
            case DATE -> "데이트하기";
            case TRAVEL -> "여행 일정 소화하기";
            case EVENT -> "이벤트 참여하기";
            case CEREMONY -> "경조사 참석하기";
            case WEDDING -> "결혼식 참석하기";
            case DRINKING -> "술자리 모임 참석하기";
            case TALK -> "친목·수다 일정 보내기";
        };
    }

    private String joinEveningConditions(Set<EveningCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return DEFAULT_VALUE;
        }
        return conditions.stream()
                .sorted(Comparator.comparing(Enum::name))
                .map(EveningCondition::getLabel)
                .collect(Collectors.joining(", "));
    }

    private SkinCondition resolveRepresentativeSkinCondition(Set<EveningCondition> conditions) {
        if (conditions == null || conditions.isEmpty() || conditions.contains(EveningCondition.NONE)) {
            return SkinCondition.NORMAL;
        }
        if (conditions.contains(EveningCondition.RED_HOT)) {
            return SkinCondition.HOT_REDNESS;
        }
        if (conditions.contains(EveningCondition.TROUBLE_OIL)) {
            return SkinCondition.TROUBLE_REDNESS;
        }
        if (conditions.contains(EveningCondition.LONG_MAKEUP)) {
            return SkinCondition.MAKEUP_BREAKDOWN;
        }
        if (conditions.contains(EveningCondition.DRY_TIGHT) || conditions.contains(EveningCondition.AC_LONG_EXPOSURE)) {
            return SkinCondition.DRY_TIGHT;
        }
        if (conditions.contains(EveningCondition.COLD_SENSITIVE) || conditions.contains(EveningCondition.STICKY_OILY)) {
            return SkinCondition.OUTDOOR_SENSITIVE;
        }
        return SkinCondition.NORMAL;
    }

    private String getAge(Integer age) {
        return age == null ? DEFAULT_VALUE : String.valueOf(age);
    }

    private String getGender(User user) {
        return openAiService.getGenderLabel(user.getGender());
    }

    private String getSkinType(SkinType skinType) {
        return skinType == null ? DEFAULT_VALUE : skinType.getLabel();
    }

    private String getGoal(String goal) {
        return goal == null || goal.isBlank() ? DEFAULT_VALUE : goal;
    }

    private String getCheckCycle(CheckCycle checkCycle) {
        return checkCycle == null ? DEFAULT_VALUE : checkCycle.getLabel();
    }

    private String getCareMotivation(User user) {
        return openAiService.getCareMotivationLabel(user.getCareMotivation());
    }

    private String getLatestDiagnosisRecommendation(Long userId) {
        return diagnosisRepository.findTopByUserIdOrderByDiagnosedAtDesc(userId)
                .map(Diagnosis::getRecommendation)
                .filter(value -> value != null && !value.isBlank())
                .orElse(DEFAULT_VALUE);
    }

    private List<Long> extractStepIds(List<GeneratedMissionStep> steps) {
        return steps.stream().map(GeneratedMissionStep::getId).toList();
    }

    private List<MorningRoutineItem> getActualMorningRoutineItems(MorningRoutine routine) {
        return morningRoutineItemRepository.findByMorningRoutineAndSourceInOrderByItemOrderAsc(
                routine,
                List.of(
                        MorningRoutineItemSource.AI,
                        MorningRoutineItemSource.SURVEY,
                        MorningRoutineItemSource.CUSTOM
                )
        ).stream().limit(MORNING_ROUTINE_SIZE).toList();
    }

    private MorningRoutineResponse toMorningRoutineResponse(MorningRoutine routine, List<MorningRoutineItem> items) {
        return MorningRoutineResponse.builder()
                .routineId(routine.getId())
                .items(items.stream().map(MorningRoutineItemResponse::from).toList())
                .build();
    }

    private void normalizeMorningRoutineItemOrder(MorningRoutine routine) {
        List<MorningRoutineItem> items = morningRoutineItemRepository.findByMorningRoutineOrderByItemOrderAsc(routine);
        if (items.isEmpty()) {
            return;
        }

        for (int index = 0; index < items.size(); index++) {
            items.get(index).updateItemOrder(MORNING_ROUTINE_TEMP_ORDER_OFFSET + index + 1);
        }
        morningRoutineItemRepository.saveAllAndFlush(items);

        for (int index = 0; index < items.size(); index++) {
            items.get(index).updateItemOrder(index + 1);
        }
        morningRoutineItemRepository.saveAllAndFlush(items);
    }
}
