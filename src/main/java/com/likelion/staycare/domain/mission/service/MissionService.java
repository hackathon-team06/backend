package com.likelion.staycare.domain.mission.service;

import com.likelion.staycare.domain.diagnosis.entity.AgeRange;
import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningMissionRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionByDateResponse;
import com.likelion.staycare.domain.mission.dto.response.MissionStepDetailResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.entity.DailySkinCheck;
import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
import com.likelion.staycare.domain.mission.entity.enums.MissionStatus;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import com.likelion.staycare.domain.mission.entity.enums.SkinCondition;
import com.likelion.staycare.domain.mission.exception.MissionErrorCode;
import com.likelion.staycare.domain.mission.repository.DailySkinCheckRepository;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionRepository;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionStepRepository;
import com.likelion.staycare.domain.mission.repository.UserMissionStepCheckRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private static final String PROMPT_VERSION = "v1";
    private static final LocalTime MORNING_END_TIME = LocalTime.NOON;

    private final UserRepository userRepository;
    private final DailySkinCheckRepository dailySkinCheckRepository;
    private final GeneratedMissionRepository generatedMissionRepository;
    private final GeneratedMissionStepRepository generatedMissionStepRepository;
    private final UserMissionStepCheckRepository userMissionStepCheckRepository;
    private final ScheduleRepository scheduleRepository;
    private final OpenAiService openAiService;

    @Transactional
    public MorningMissionResponse generateMorningMission(Long userId) {
        User user = getUser(userId);
        LocalDate today = LocalDate.now();

        GeneratedMission existingMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, today, MissionTime.MORNING)
                .orElse(null);

        if (existingMission != null) {
            return toMorningMissionResponse(existingMission);
        }

        validateMorningMissionTime();

        LocalDate yesterday = today.minusDays(1);
        GeneratedMission previousEveningMission = generatedMissionRepository
                .findByUserAndMissionDateAndMissionTime(user, yesterday, MissionTime.EVENING)
                .orElse(null);
        List<UserMissionStepCheck> previousChecks = previousEveningMission == null
                ? List.of()
                : userMissionStepCheckRepository.findByGeneratedMissionStepGeneratedMission(previousEveningMission);
        DailySkinCheck previousSkinCheck = dailySkinCheckRepository
                .findByUserAndCheckedDate(user, yesterday)
                .orElse(null);

        MorningMissionRequest request = new MorningMissionRequest(
                getAge(user.getAgeRange()),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getTodayScheduleContext(userId, today),
                previousEveningMission == null ? DEFAULT_VALUE : previousEveningMission.getTitle(),
                buildPreviousMissionResult(previousChecks),
                previousSkinCheck == null ? DEFAULT_VALUE : previousSkinCheck.getSkinCondition().name()
        );

        MorningMissionResponse response = openAiService.generateMorningMission(request);
        List<String> steps = buildMissionStepsWithSchedule(userId, today, response.steps());
        GeneratedMission savedMission = saveGeneratedMission(
                user,
                null,
                response.title(),
                response.description(),
                response.tip(),
                today,
                MissionTime.MORNING
        );
        List<GeneratedMissionStep> savedSteps = saveGeneratedMissionSteps(savedMission, steps);

        return MorningMissionResponse.builder()
                .title(response.title())
                .description(response.description())
                .stepIds(extractStepIds(savedSteps))
                .steps(steps)
                .tip(response.tip())
                .build();
    }

    @Transactional
    public EveningMissionResponse generateEveningMission(Long userId, SkinCondition skinCondition) {
        User user = getUser(userId);
        LocalDate today = LocalDate.now();

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
                getAge(user.getAgeRange()),
                getSkinType(user.getSkinType()),
                getGoal(user.getGoal()),
                getCheckCycle(user.getCheckCycle()),
                getTodayScheduleContext(userId, today),
                dailySkinCheck.getSkinCondition().name()
        );

        EveningMissionResponse response = openAiService.generateEveningMission(request);
        List<String> steps = buildMissionStepsWithSchedule(userId, today, response.steps());
        GeneratedMission savedMission = saveGeneratedMission(
                user,
                dailySkinCheck,
                response.title(),
                response.description(),
                response.tip(),
                today,
                MissionTime.EVENING
        );
        List<GeneratedMissionStep> savedSteps = saveGeneratedMissionSteps(savedMission, steps);

        return EveningMissionResponse.builder()
                .title(response.title())
                .description(response.description())
                .stepIds(extractStepIds(savedSteps))
                .steps(steps)
                .tip(response.tip())
                .build();
    }

    public TodayMissionResponse getTodayMissions(Long userId) {
        User user = getUser(userId);
        LocalDate today = LocalDate.now();

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
        User user = getUser(userId);
        GeneratedMissionStep generatedMissionStep = generatedMissionStepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_STEP_NOT_FOUND));

        GeneratedMission mission = generatedMissionStep.getGeneratedMission();
        validateMissionOwner(user, mission);

        UserMissionStepCheck stepCheck = userMissionStepCheckRepository.findByGeneratedMissionStepId(stepId)
                .orElseGet(() -> UserMissionStepCheck.builder()
                        .generatedMissionStep(generatedMissionStep)
                        .build());

        stepCheck.updateChecked(true);
        userMissionStepCheckRepository.save(stepCheck);

        if (isAllStepsCompleted(mission)) {
            mission.complete();
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

    private List<String> buildMissionStepsWithSchedule(Long userId, LocalDate missionDate, List<String> aiSteps) {
        List<String> steps = new ArrayList<>(aiSteps);
        scheduleRepository.findByUserIdAndScheduleDateAndStatus(userId, missionDate, ScheduleStatus.ACTIVE)
                .map(this::buildScheduleStep)
                .ifPresent(steps::add);
        return steps;
    }

    private void validateMorningMissionTime() {
        if (!LocalTime.now().isBefore(MORNING_END_TIME)) {
            throw new CustomException(MissionErrorCode.MORNING_MISSION_TIME_CLOSED);
        }
    }

    private void validateEveningMissionTime() {
        if (LocalTime.now().isBefore(MORNING_END_TIME)) {
            throw new CustomException(MissionErrorCode.EVENING_MISSION_TIME_ONLY);
        }
    }

    private boolean isAllStepsCompleted(GeneratedMission mission) {
        List<GeneratedMissionStep> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission);
        List<UserMissionStepCheck> checks = userMissionStepCheckRepository.findByGeneratedMissionStepGeneratedMission(mission);

        if (steps.isEmpty() || steps.size() != checks.size()) {
            return false;
        }

        return checks.stream().allMatch(UserMissionStepCheck::isChecked);
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
                .collect(Collectors.toMap(
                        check -> check.getGeneratedMissionStep().getId(),
                        Function.identity()
                ));

        return missionSteps.stream()
                .map(step -> MissionStepDetailResponse.builder()
                        .stepId(step.getId())
                        .stepOrder(step.getStepOrder())
                        .content(step.getContent())
                        .completed(checksByStepId.get(step.getId()) != null && checksByStepId.get(step.getId()).isChecked())
                        .build())
                .toList();
    }

    private String buildPreviousMissionResult(List<UserMissionStepCheck> checks) {
        if (checks.isEmpty()) {
            return DEFAULT_VALUE;
        }

        long checkedCount = checks.stream()
                .filter(UserMissionStepCheck::isChecked)
                .count();

        return checkedCount + "/" + checks.size() + " 단계 완료";
    }

    private String getAge(AgeRange ageRange) {
        return ageRange == null ? DEFAULT_VALUE : ageRange.getLabel();
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
        return steps.stream()
                .map(GeneratedMissionStep::getId)
                .toList();
    }

    private String getTodayScheduleContext(Long userId, LocalDate missionDate) {
        return scheduleRepository.findByUserIdAndScheduleDateAndStatus(userId, missionDate, ScheduleStatus.ACTIVE)
                .map(this::buildScheduleContext)
                .orElse(DEFAULT_VALUE);
    }

    private String buildScheduleContext(Schedule schedule) {
        String title = schedule.getTitle();
        String companion = getCompanionText(schedule.getCompanion());
        String category = getCategoryText(schedule.getCategory());
        String startTime = schedule.getStartTime() == null ? DEFAULT_VALUE : schedule.getStartTime().toString();
        String endTime = schedule.getEndTime() == null ? DEFAULT_VALUE : schedule.getEndTime().toString();

        if (schedule.getCompanion() == Companion.ALONE) {
            return "일정 제목: " + title + ", 일정 유형: " + category + ", 시작 시각: " + startTime + ", 종료 시각: " + endTime;
        }

        return "일정 제목: " + title + ", 동행자: " + companion + ", 일정 유형: " + category
                + ", 시작 시각: " + startTime + ", 종료 시각: " + endTime;
    }

    private String buildScheduleStep(Schedule schedule) {
        String companion = getCompanionText(schedule.getCompanion());
        String category = getCategoryText(schedule.getCategory());

        if (schedule.getCompanion() == Companion.ALONE) {
            return category;
        }

        return companion + "와 " + category;
    }

    private String getCompanionText(Companion companion) {
        return switch (companion) {
            case ALONE -> "혼자";
            case LOVER -> "연인";
            case COWORKER -> "직장동료";
            case FRIEND -> "친구";
            case FAMILY -> "가족";
            case ACQUAINTANCE -> "지인";
        };
    }

    private String getCategoryText(ScheduleCategory category) {
        return switch (category) {
            case DATE -> "데이트하기";
            case MEETING -> "미팅 참석하기";
            case SELF_CARE -> "자기관리 시간 보내기";
            case DRINKING -> "술자리 참석하기";
            case TRAVEL -> "여행 다녀오기";
            case WEDDING -> "결혼식 참석하기";
            case EVENT -> "행사 참석하기";
            case TALK -> "대화 나누기";
            case CEREMONY -> "경조사 참석하기";
        };
    }
}
