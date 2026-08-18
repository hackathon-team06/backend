package com.likelion.staycare.domain.stamp.service;

import com.likelion.staycare.domain.diagnosis.entity.CheckCycle;
import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.enums.MissionStatus;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionRepository;
import com.likelion.staycare.domain.mission.repository.GeneratedMissionStepRepository;
import com.likelion.staycare.domain.mission.repository.UserMissionStepCheckRepository;
import com.likelion.staycare.domain.point.entity.PointHistory;
import com.likelion.staycare.domain.point.entity.PointRewardType;
import com.likelion.staycare.domain.point.repository.PointHistoryRepository;
import com.likelion.staycare.domain.stamp.dto.MyPageStampSummaryResponse;
import com.likelion.staycare.domain.stamp.dto.StampBookCardResponse;
import com.likelion.staycare.domain.stamp.dto.StampCalendarDayResponse;
import com.likelion.staycare.domain.stamp.dto.StampCalendarResponse;
import com.likelion.staycare.domain.stamp.dto.StampCalendarSummaryResponse;
import com.likelion.staycare.domain.stamp.entity.StampBook;
import com.likelion.staycare.domain.stamp.entity.StampBookStatus;
import com.likelion.staycare.domain.stamp.repository.StampBookRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StampService {

    private static final String FULL_SUCCESS = "FULL_SUCCESS";
    private static final String PARTIAL_SUCCESS = "PARTIAL_SUCCESS";
    private static final String NOT_DONE = "NOT_DONE";

    private static final String CARD_IN_PROGRESS = "IN_PROGRESS";
    private static final String CARD_COMPLETED = "COMPLETED";

    private static final int DAILY_COMPLETE_BONUS_POINT = 2;
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final GeneratedMissionRepository generatedMissionRepository;
    private final GeneratedMissionStepRepository generatedMissionStepRepository;
    private final UserMissionStepCheckRepository userMissionStepCheckRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final StampBookRepository stampBookRepository;

    public StampCalendarResponse getStampCalendar(Long userId, Integer year, Integer month) {
        validateYearMonth(year, month);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<GeneratedMission> missions =
                generatedMissionRepository.findAllByUserAndMissionDateBetweenOrderByMissionDateAscMissionTimeAsc(
                        user, startDate, endDate
                );

        Map<LocalDate, List<GeneratedMission>> missionsByDate = missions.stream()
                .collect(Collectors.groupingBy(
                        GeneratedMission::getMissionDate,
                        TreeMap::new,
                        Collectors.toList()
                ));

        List<GeneratedMissionStep> allSteps = missions.isEmpty()
                ? List.of()
                : generatedMissionStepRepository.findAllByGeneratedMissionIn(missions);

        Set<Long> checkedStepIds = allSteps.isEmpty()
                ? Set.of()
                : new HashSet<>(userMissionStepCheckRepository.findCheckedStepIds(allSteps));

        Map<LocalDate, List<GeneratedMissionStep>> stepsByDate = allSteps.stream()
                .collect(Collectors.groupingBy(
                        step -> step.getGeneratedMission().getMissionDate(),
                        TreeMap::new,
                        Collectors.toList()
                ));

        Map<LocalDate, Integer> completionPointByDate = getCompletionPointByDate(user, startDate, endDate);

        List<StampCalendarDayResponse> days = new ArrayList<>();

        int totalStampCount = 0;
        int totalDailyPoint = 0;
        int totalCompletionPoint = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<GeneratedMission> dailyMissions = missionsByDate.getOrDefault(date, List.of());
            List<GeneratedMissionStep> dailySteps = stepsByDate.getOrDefault(date, List.of());

            int completedStepCount = (int) dailySteps.stream()
                    .map(GeneratedMissionStep::getId)
                    .filter(checkedStepIds::contains)
                    .count();

            boolean morningCompleted = dailyMissions.stream()
                    .anyMatch(m -> m.getMissionTime() == MissionTime.MORNING
                            && m.getStatus() == MissionStatus.COMPLETED);

            boolean eveningCompleted = dailyMissions.stream()
                    .anyMatch(m -> m.getMissionTime() == MissionTime.EVENING
                            && m.getStatus() == MissionStatus.COMPLETED);

            boolean fullSuccess = morningCompleted && eveningCompleted;

            String status = resolveDailyStatus(completedStepCount, fullSuccess);

            int dailyPoint = completedStepCount + (fullSuccess ? DAILY_COMPLETE_BONUS_POINT : 0);
            int completionPoint = completionPointByDate.getOrDefault(date, 0);
            int totalPoint = dailyPoint + completionPoint;

            if (!NOT_DONE.equals(status)) {
                totalStampCount += 1;
            }

            totalDailyPoint += dailyPoint;
            totalCompletionPoint += completionPoint;

            days.add(new StampCalendarDayResponse(
                    date,
                    status,
                    totalPoint
            ));
        }

        StampCalendarSummaryResponse summary = new StampCalendarSummaryResponse(
                totalStampCount,
                totalDailyPoint,
                totalCompletionPoint,
                totalDailyPoint + totalCompletionPoint
        );

        return new StampCalendarResponse(
                year,
                month,
                summary,
                days
        );
    }

    @Transactional
    public MyPageStampSummaryResponse getMyPageStampSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        LocalDate today = LocalDate.now(KOREA_ZONE_ID);

        List<StampBook> stampBooks = stampBookRepository.findAllByUserOrderByStartDateAsc(user);

        if (stampBooks.isEmpty()) {
            int selectedPeriodDays = resolveStampPeriodDays(user);

            StampBook newBook = StampBook.builder()
                    .user(user)
                    .startDate(today)
                    .endDate(today.plusDays(selectedPeriodDays - 1L))
                    .periodDays(selectedPeriodDays)
                    .build();

            stampBookRepository.save(newBook);
            stampBooks = List.of(newBook);
        }

        List<StampBookCardResponse> cards = stampBooks.stream()
                .map(book -> toCardResponse(book, today))
                .toList();

        int totalStampBookCount = cards.size();

        int completedStampBookCount = (int) stampBooks.stream()
                .filter(book -> book.getStatus() == StampBookStatus.SETTLED
                        || book.getStatus() == StampBookStatus.COMPLETED)
                .count();

        return new MyPageStampSummaryResponse(
                totalStampBookCount,
                completedStampBookCount,
                cards
        );
    }

    private void validateYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new IllegalArgumentException("year와 month는 필수입니다.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month는 1~12 사이여야 합니다.");
        }
    }

    private String resolveDailyStatus(int completedStepCount, boolean fullSuccess) {
        if (completedStepCount == 0) {
            return NOT_DONE;
        }
        if (fullSuccess) {
            return FULL_SUCCESS;
        }
        return PARTIAL_SUCCESS;
    }

    private Map<LocalDate, Integer> getCompletionPointByDate(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        List<PointHistory> histories = pointHistoryRepository
                .findAllByUserAndRewardTypeAndCreatedAtBetween(
                        user,
                        PointRewardType.STAMP_COMPLETION,
                        from,
                        to
                );

        return histories.stream()
                .collect(Collectors.groupingBy(
                        history -> history.getCreatedAt().toLocalDate(),
                        TreeMap::new,
                        Collectors.summingInt(PointHistory::getAmount)
                ));
    }

    private StampBookCardResponse toCardResponse(StampBook stampBook, LocalDate today) {
        String cardStatus = resolveCardStatus(stampBook);
        int progressDays = calculateProgressDays(stampBook.getStartDate(), today, stampBook.getPeriodDays());
        String displayText = resolveDisplayText(cardStatus, progressDays, stampBook.getPeriodDays());

        return new StampBookCardResponse(
                stampBook.getStartDate(),
                stampBook.getEndDate(),
                stampBook.getPeriodDays(),
                cardStatus,
                progressDays,
                displayText
        );
    }

    private String resolveCardStatus(StampBook stampBook) {
        if (stampBook.getStatus() == StampBookStatus.IN_PROGRESS) {
            return CARD_IN_PROGRESS;
        }
        return CARD_COMPLETED;
    }

    private int calculateProgressDays(LocalDate startDate, LocalDate today, int periodDays) {
        int days = (int) (ChronoUnit.DAYS.between(startDate, today) + 1);

        if (days < 0) {
            return 0;
        }

        return Math.min(days, periodDays);
    }

    private String resolveDisplayText(String cardStatus, int progressDays, int periodDays) {
        if (CARD_COMPLETED.equals(cardStatus)) {
            return periodDays + "DAY";
        }
        return progressDays + "일째 진행중";
    }

    private int resolveStampPeriodDays(User user) {
        CheckCycle checkCycle = user.getCheckCycle();

        if (checkCycle == null) {
            return CheckCycle.DAYS_7.getDays();
        }

        return checkCycle.getDays();
    }
}
