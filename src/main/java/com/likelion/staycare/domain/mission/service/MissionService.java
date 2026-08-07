package com.likelion.staycare.domain.mission.service;

import com.likelion.staycare.domain.diagnosis.entity.AgeRange;
import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.mission.dto.request.EveningMissionRequest;
import com.likelion.staycare.domain.mission.dto.request.MorningMissionRequest;
import com.likelion.staycare.domain.mission.dto.response.EveningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.MorningMissionResponse;
import com.likelion.staycare.domain.mission.dto.response.TodayMissionResponse;
import com.likelion.staycare.domain.mission.entity.DailySkinCheck;
import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
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
        saveGeneratedMissionSteps(savedMission, steps);

        return MorningMissionResponse.builder()
                .title(response.title())
                .description(response.description())
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
        saveGeneratedMissionSteps(savedMission, steps);

        return EveningMissionResponse.builder()
                .title(response.title())
                .description(response.description())
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

    @Transactional
    public void completeStep(Long userId, Long stepId) {
        User user = getUser(userId);
        GeneratedMissionStep generatedMissionStep = generatedMissionStepRepository.findById(stepId)
                .orElseThrow(() -> new CustomException(MissionErrorCode.MISSION_STEP_NOT_FOUND));

        GeneratedMission mission = generatedMissionStep.getGeneratedMission();
        if (!mission.getUser().getId().equals(user.getId())) {
            throw new CustomException(MissionErrorCode.FORBIDDEN_MISSION);
        }

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

    private void saveGeneratedMissionSteps(GeneratedMission mission, List<String> steps) {
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

        generatedMissionStepRepository.saveAll(generatedMissionSteps);
    }

    private List<String> buildMissionStepsWithSchedule(Long userId, LocalDate missionDate, List<String> aiSteps) {
        List<String> steps = new ArrayList<>(aiSteps);
        scheduleRepository.findByUserIdAndScheduleDate(userId, missionDate)
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
        List<String> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission)
                .stream()
                .map(GeneratedMissionStep::getContent)
                .toList();

        return MorningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .steps(steps)
                .tip(mission.getTip())
                .build();
    }

    private EveningMissionResponse toEveningMissionResponse(GeneratedMission mission) {
        List<String> steps = generatedMissionStepRepository.findByGeneratedMissionOrderByStepOrderAsc(mission)
                .stream()
                .map(GeneratedMissionStep::getContent)
                .toList();

        return EveningMissionResponse.builder()
                .title(mission.getTitle())
                .description(mission.getDescription())
                .steps(steps)
                .tip(mission.getTip())
                .build();
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

    private String buildScheduleStep(Schedule schedule) {
        String companion = getCompanionText(schedule.getCompanion());

        if (companion.endsWith("인")) {
            return companion + "과 " + getCategoryText(schedule.getCategory());
        }

        return companion + "와 " + getCategoryText(schedule.getCategory());
    }

    private String getCompanionText(Companion companion) {
        return switch (companion) {
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
            case SELF_CARE -> "셀프케어 시간 보내기";
            case DRINKING -> "술자리 참석하기";
            case TRAVEL -> "여행 다녀오기";
            case WEDDING -> "결혼식 참석하기";
            case EVENT -> "행사 참석하기";
            case TALK -> "대화 나누기";
            case CEREMONY -> "경조사 참석하기";
        };
    }
}
