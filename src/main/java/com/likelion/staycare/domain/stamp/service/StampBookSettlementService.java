package com.likelion.staycare.domain.stamp.service;

import com.likelion.staycare.domain.point.service.PointService;
import com.likelion.staycare.domain.stamp.entity.StampBook;
import com.likelion.staycare.domain.stamp.entity.StampBookStatus;
import com.likelion.staycare.domain.stamp.entity.StampDailyStatus;
import com.likelion.staycare.domain.stamp.entity.StampRewardPolicy;
import com.likelion.staycare.domain.stamp.repository.StampBookRepository;
import com.likelion.staycare.domain.stamp.repository.StampDayRepository;
import com.likelion.staycare.domain.stamp.repository.StampRewardPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class StampBookSettlementService {

    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");

    /**
     * 진행도 집계에 어떤 상태를 포함할지 결정하는 핵심 지점
     * - FULL_SUCCESS만 완료일로 볼지
     * - PARTIAL_SUCCESS도 포함할지
     * 여기서 조절하면 됨
     */
    private static final Set<StampDailyStatus> PROGRESS_STATUSES =
            EnumSet.of(StampDailyStatus.FULL_SUCCESS);

    private final StampBookRepository stampBookRepository;
    private final StampDayRepository stampDayRepository;
    private final StampRewardPolicyRepository stampRewardPolicyRepository;
    private final PointService pointService;

    /**
     * 매일 00:10 KST에 전날까지 종료된 스탬프북 정산
     * endDate가 오늘보다 이전인 경우만 정산해서,
     * 마지막 날 하루가 끝난 뒤에 보상 지급되도록 함
     */
    @Transactional
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    public void settleExpiredStampBooks() {
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);

        List<StampBook> targetBooks =
                stampBookRepository.findAllByStatusAndEndDateBefore(StampBookStatus.IN_PROGRESS, today);

        if (targetBooks.isEmpty()) {
            log.info("[StampBookSettlementService] 정산 대상 스탬프북 없음 - today={}", today);
            return;
        }

        log.info("[StampBookSettlementService] 정산 시작 - today={}, count={}", today, targetBooks.size());

        for (StampBook stampBook : targetBooks) {
            settleOneBook(stampBook);
        }

        log.info("[StampBookSettlementService] 정산 종료 - today={}, count={}", today, targetBooks.size());
    }

    /**
     * 필요하면 수동 정산용으로도 호출 가능
     */
    @Transactional
    public void settleOneBook(Long stampBookId) {
        StampBook stampBook = stampBookRepository.findById(stampBookId)
                .orElseThrow(() -> new IllegalArgumentException("스탬프북을 찾을 수 없습니다. stampBookId=" + stampBookId));

        settleOneBook(stampBook);
    }

    private void settleOneBook(StampBook stampBook) {
        if (stampBook.getStatus() != StampBookStatus.IN_PROGRESS) {
            return;
        }

        int completedDays = stampDayRepository.countProgressDays(
                stampBook.getId(),
                PROGRESS_STATUSES
        );

        StampRewardPolicy rewardPolicy = stampRewardPolicyRepository
                .findByPeriodDaysAndCompletedDays(stampBook.getPeriodDays(), completedDays)
                .orElseThrow(() -> new IllegalStateException(
                        "스탬프 보상 정책이 없습니다. periodDays="
                                + stampBook.getPeriodDays()
                                + ", completedDays="
                                + completedDays
                ));

        int rewardPoint = rewardPolicy.getRewardPoint();

        int actualRewardedPoint = pointService.rewardStampCompletion(
                stampBook.getUser(),
                stampBook.getId(),
                rewardPoint
        );

        stampBook.settle(
                completedDays,
                actualRewardedPoint,
                LocalDateTime.now(KOREA_ZONE_ID)
        );

        log.info(
                "[StampBookSettlementService] 스탬프북 정산 완료 - stampBookId={}, userId={}, periodDays={}, completedDays={}, rewardPoint={}",
                stampBook.getId(),
                stampBook.getUser().getId(),
                stampBook.getPeriodDays(),
                completedDays,
                actualRewardedPoint
        );
    }
}
