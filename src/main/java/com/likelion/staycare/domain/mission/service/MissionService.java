package com.likelion.staycare.domain.mission.service;

import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineRecommendationRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningRoutineSaveRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineItemResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineRecommendationResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningRoutineResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.entity.DailySkinCheck;
import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.MorningRoutine;
import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
import com.likelion.staycare.domain.mission.entity.enums.MissionStatus;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private static final String DEFAULT_VALUE = "없음";
    private static final String MORNING_TITLE = "오늘 아침 루틴";
    private static final String MORNING_DESCRIPTION = "사용자가 저장한 고정 아침 루틴입니다.";
    private static final String MORNING_TIP = "아침 루틴은 짧게 끝내더라도 매일 같은 순서로 유지해보세요.";
    private static final String PROMPT_VERSION = "v1";
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
    private final OpenAiService openAiService;
    private final PointService pointService;

    public MorningRoutineRecommendationResponse recommendMorningRoutine(
            Long userId,
            MorningRoutineRecommendationRequest request
    ) {
        User user = getUser(userId);
        List<String> existingRoutines = morningRoutineRepository.findByUserId(userId)
                .map(morningRoutineItemRepository::findByMorningRoutineOrderByItemOrderAsc)
                .orElse(List.of())
                .stream()
                .map(MorningRoutineItem::getContent)
                .toList();

        int recommendationCount = existingRoutines.isEmpty() ? 6 : 4;
        List<String> recommendations = openAiService.recommendMorningRoutine(
                getAge(user.getAge()),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                request.categories(),
                existingRoutines,
                recommendationCount
        );

        return MorningRoutineRecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    @Transactional
    public MorningRoutineResponse saveMorningRoutine(Long userId, MorningRoutineSaveRequest request) {
        if (request.items() == null || request.items().isEmpty() || request.items().size() > 3) {
            throw new CustomException(MissionErrorCode.INVALID_MORNING_ROUTINE_SIZE);
        }

        User user = getUser(userId);
        MorningRoutine routine = morningRoutineRepository.findByUserId(userId)
                .orElseGet(() -> morningRoutineRepository.save(MorningRoutine.builder().user(user).build()));

        morningRoutineItemRepository.deleteByMorningRoutine(routine);

        List<MorningRoutineItem> items = new ArrayList<>();
        for (int index = 0; index < request.items().size(); index++) {
            var itemRequest = request.items().get(index);
            items.add(MorningRoutineItem.builder()
                    .morningRoutine(routine)
                    .content(itemRequest.content())
                    .category(itemRequest.category())
                    .source(itemRequest.source())
                    .itemOrder(index + 1)
                    .build());
        }

        List<MorningRoutineItem> savedItems = morningRoutineItemRepository.saveAll(items);
        return toMorningRoutineResponse(routine, savedItems);
    }

    public MorningRoutineResponse getMorningRoutine(Long userId) {
        MorningRoutine routine = morningRoutineRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND));

        return toMorningRoutineResponse(
                routine,
                morningRoutineItemRepository.findByMorningRoutineOrderByItemOrderAsc(routine)
        );
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

        return toMorningRoutineResponse(
                routine,
                morningRoutineItemRepository.findByMorningRoutineOrderByItemOrderAsc(routine)
        );
    }

    @Transactional
    public MorningMissionResponse generateMorningMission(Long userId) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission existingMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.MORNING)
                .orElse(null);

        if (existingMission != null) {
            return toMorningMissionResponse(existingMission);
        }

        validateMorningMissionTime();

        MorningRoutine routine = morningRoutineRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MORNING_ROUTINE_NOT_FOUND));

        List<String> steps = morningRoutineItemRepository.findByMorningRoutineOrderByItemOrderAsc(routine)
                .stream()
                .map(MorningRoutineItem::getContent)
                .collect(Collectors.toCollection(ArrayList::new));

        scheduleRepository.findFirstByUserIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStatusOrderByStartDateAsc(
                userId,
                today,
                today,
                ScheduleStatus.ACTIVE
        ).map(this::buildScheduleStep)
                .ifPresent(steps::add);

        GeneratedMission savedMission = saveGeneratedMission(
                user,
                null,
                MORNING_TITLE,
                MORNING_DESCRIPTION,
                MORNING_TIP,
                today,
                MissionTime.MORNING
        );
        List<GeneratedMissionStep> savedSteps = saveGeneratedMissionSteps(savedMission, steps);

        return MorningMissionResponse.builder()
                .title(MORNING_TITLE)
                .description(MORNING_DESCRIPTION)
                .stepIds(extractStepIds(savedSteps))
                .steps(steps)
                .tip(MORNING_TIP)
                .build();
    }

    @Transactional
    public EveningMissionResponse generateEveningMission(Long userId, SkinCondition skinCondition) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission existingMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElse(null);

        if (existingMission != null) {
            return toEveningMissionResponse(existingMission);
        }

        validateEveningMissionTime();

        DailySkinCheck dailySkinCheck = dailySkinCheckRepository.findByUserAndCheckedDate(user, today)
                .orElseGet(() -> dailySkinCheckRepository.save(
                        DailySkinCheck.builder()
                                .user(user)
                                .skinCondition(skinCondition)
                                .checkedDate(today)
                                .build()
                ));

        EveningMissionRequest request = new EveningMissionRequest(
                getAge(user.getAge()),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getTodayScheduleContext(userId, today),
                dailySkinCheck.getSkinCondition().name(),
                buildTodayMorningMissionStatus(user)
        );

        EveningMissionResponse response = openAiService.generateEveningMission(request);
        GeneratedMission savedMission = saveGeneratedMission(
                user,
                dailySkinCheck,
                response.title(),
                response.description(),
                response.tip(),
                today,
                MissionTime.EVENING
        );
        List<GeneratedMissionStep> savedSteps = saveGeneratedMissionSteps(savedMission, response.steps());

        return EveningMissionResponse.builder()
                .title(response.title())
                .description(response.description())
                .stepIds(extractStepIds(savedSteps))
                .steps(response.steps())
                .tip(response.tip())
                .build();
    }

    public TodayMissionResponse getTodayMissions(Long userId) {
        User user = getUser(userId);
        LocalDate today = getCurrentDate();

        GeneratedMission morningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.MORNING)
                .orElse(null);
        GeneratedMission eveningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.EVENING)
                .orElse(null);

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

    @Transactional
    public void completeStep(Long userId, Long stepId) {
        try {
            User user = getUser(userId);
            GeneratedMissionStep generatedMissionStep = generatedMissionStepRepository.findById(stepId)
                    .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_STEP_NOT_FOUND));

            GeneratedMission mission = generatedMissionStep.getGeneratedMission();
            validateMissionOwner(user, mission);

            UserMissionStepCheck stepCheck = userMissionStepCheckRepository.findByGeneratedMissionStepId(stepId)
                    .orElseGet(() -> UserMissionStepCheck.builder().generatedMissionStep(generatedMissionStep).build());

            boolean alreadyChecked = stepCheck.isChecked();
            stepCheck.updateChecked(true);
            userMissionStepCheckRepository.saveAndFlush(stepCheck);

            if (!alreadyChecked && isPointRewardStep(generatedMissionStep)) {
                pointService.rewardMissionStep(user, generatedMissionStep);
            }

            if (isAllPointRewardStepsCompleted(mission)) {
                pointService.rewardMissionCompleteBonus(user, mission);
            }

            if (isAllStepsCompleted(mission)) {
                mission.complete();
                generatedMissionRepository.saveAndFlush(mission);
            }
        } catch (CustomException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new CustomException(MissionErrorCode.MISSION_STEP_PROCESS_FAILED);
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
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

    private GeneratedMission saveGeneratedMission(
            User user,
            DailySkinCheck dailySkinCheck,
            String title,
            String description,
            String tip,
            LocalDate missionDate,
            MissionTime missionTime
    ) {
        return generatedMissionRepository.save(
                GeneratedMission.builder()
                        .user(user)
                        .dailySkinCheck(dailySkinCheck)
                        .title(title)
                        .description(description)
                        .tip(tip)
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

    private void validateMorningMissionTime() {
        if (!getCurrentTime().isBefore(MORNING_END_TIME)) {
            throw new CustomException(MissionErrorCode.MORNING_MISSION_TIME_CLOSED);
        }
    }

    private void validateEveningMissionTime() {
        if (getCurrentTime().isBefore(MORNING_END_TIME)) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_TIME_ONLY);
        }
    }

    private LocalDate getCurrentDate() {
        return LocalDate.now(KOREA_ZONE_ID);
    }

    private LocalTime getCurrentTime() {
        return LocalTime.now(KOREA_ZONE_ID);
    }

    private boolean isAllStepsCompleted(GeneratedMission mission) {
        List<GeneratedMissionStep> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        List<UserMissionStepCheck> checks = userMissionStepCheckRepository.findByGeneratedMissionStepGeneratedMission(mission);

        if (steps.isEmpty() || steps.size() != checks.size()) {
            return false;
        }

        return checks.stream().allMatch(UserMissionStepCheck::isChecked);
    }

    private boolean isAllPointRewardStepsCompleted(GeneratedMission mission) {
        List<GeneratedMissionStep> rewardSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission)
                .stream()
                .filter(this::isPointRewardStep)
                .toList();
        List<UserMissionStepCheck> checks = userMissionStepCheckRepository.findByGeneratedMissionStepGeneratedMission(mission);

        if (rewardSteps.isEmpty()) {
            return false;
        }

        Map<Long, UserMissionStepCheck> checksByStepId = checks.stream()
                .collect(Collectors.toMap(check -> check.getGeneratedMissionStep().getId(), Function.identity()));

        return rewardSteps.stream().allMatch(step ->
                checksByStepId.get(step.getId()) != null && checksByStepId.get(step.getId()).isChecked()
        );
    }

    private boolean isPointRewardStep(GeneratedMissionStep step) {
        return step.getStepOrder() <= 3;
    }

    private MorningMissionResponse toMorningMissionResponse(GeneratedMission mission) {
        List<GeneratedMissionStep> missionSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);

        return MorningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .stepIds(extractStepIds(missionSteps))
                .steps(missionSteps.stream().map(GeneratedMissionStep::getContent).toList())
                .tip(mission.getTip())
                .build();
    }

    private EveningMissionResponse toEveningMissionResponse(GeneratedMission mission) {
        List<GeneratedMissionStep> missionSteps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);

        return EveningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .stepIds(extractStepIds(missionSteps))
                .steps(missionSteps.stream().map(GeneratedMissionStep::getContent).toList())
                .tip(mission.getTip())
                .build();
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

    private String buildTodayMorningMissionStatus(User user) {
        GeneratedMission morningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, getCurrentDate(), MissionTime.MORNING)
                .orElse(null);

        if (morningMission == null) {
            return DEFAULT_VALUE;
        }

        List<GeneratedMissionStep> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(morningMission)
                .stream()
                .filter(this::isPointRewardStep)
                .toList();
        Map<Long, UserMissionStepCheck> checksByStepId = userMissionStepCheckRepository
                .findByGeneratedMissionStepGeneratedMission(morningMission)
                .stream()
                .collect(Collectors.toMap(check -> check.getGeneratedMissionStep().getId(), Function.identity()));

        return steps.stream()
                .map(step -> step.getStepOrder() + ". " + step.getContent() + " - "
                        + ((checksByStepId.get(step.getId()) != null && checksByStepId.get(step.getId()).isChecked()) ? "완료" : "미완료"))
                .collect(Collectors.joining(", "));
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
        return "일정 제목: " + schedule.getTitle()
                + ", 일정 시작일: " + schedule.getStartDate()
                + ", 일정 종료일: " + schedule.getEndDate()
                + ", 동행자: " + schedule.getCompanion()
                + ", 일정 유형: " + schedule.getCategory();
    }

    private String buildScheduleStep(Schedule schedule) {
        return schedule.getTitle() + " 일정 소화하기";
    }

    private String getAge(Integer age) {
        return age == null ? DEFAULT_VALUE : String.valueOf(age);
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

    private List<Long> extractStepIds(List<GeneratedMissionStep> steps) {
        return steps.stream().map(GeneratedMissionStep::getId).toList();
    }

    private MorningRoutineResponse toMorningRoutineResponse(MorningRoutine routine, List<MorningRoutineItem> items) {
        return MorningRoutineResponse.builder()
                .routineId(routine.getId())
                .items(items.stream().map(MorningRoutineItemResponse::from).toList())
                .build();
    }
}
