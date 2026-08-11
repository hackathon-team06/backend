package com.likelion.staycare.domain.point.service;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import com.likelion.staycare.domain.point.dto.response.PointResponse;
import com.likelion.staycare.domain.point.entity.PointHistory;
import com.likelion.staycare.domain.point.entity.PointRewardType;
import com.likelion.staycare.domain.point.entity.PointWallet;
import com.likelion.staycare.domain.point.repository.PointHistoryRepository;
import com.likelion.staycare.domain.point.repository.PointWalletRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private static final int STEP_POINT = 1;
    private static final int COMPLETE_BONUS_POINT = 2;

    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointWalletRepository pointWalletRepository;

    public PointResponse getMyPoint(Long userId) {
        validateUserExists(userId);

        return PointResponse.builder()
                .point(pointWalletRepository.findByUserId(userId)
                        .map(PointWallet::getPoint)
                        .orElse(0))
                .build();
    }

    @Transactional
    public void rewardMissionStep(User user, GeneratedMissionStep step) {
        if (pointHistoryRepository.existsByUserIdAndStepIdAndRewardType(
                user.getId(),
                step.getId(),
                PointRewardType.MISSION_STEP
        )) {
            return;
        }

        try {
            pointHistoryRepository.saveAndFlush(
                    PointHistory.builder()
                            .user(user)
                            .mission(step.getGeneratedMission())
                            .step(step)
                            .rewardType(PointRewardType.MISSION_STEP)
                            .amount(STEP_POINT)
                            .build()
            );
            getOrCreatePointWallet(user).addPoint(STEP_POINT);
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @Transactional
    public void rewardMissionCompleteBonus(User user, GeneratedMission mission) {
        PointRewardType rewardType = getCompleteBonusRewardType(mission.getMissionTime());

        if (pointHistoryRepository.existsByUserIdAndMissionIdAndRewardType(user.getId(), mission.getId(), rewardType)) {
            return;
        }

        try {
            pointHistoryRepository.saveAndFlush(
                    PointHistory.builder()
                            .user(user)
                            .mission(mission)
                            .rewardType(rewardType)
                            .amount(COMPLETE_BONUS_POINT)
                            .build()
            );
            getOrCreatePointWallet(user).addPoint(COMPLETE_BONUS_POINT);
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private PointWallet getOrCreatePointWallet(User user) {
        return pointWalletRepository.findByUserId(user.getId())
                .orElseGet(() -> pointWalletRepository.save(
                        PointWallet.builder()
                                .user(user)
                                .point(0)
                                .build()
                ));
    }

    private PointRewardType getCompleteBonusRewardType(MissionTime missionTime) {
        return missionTime == MissionTime.MORNING
                ? PointRewardType.MORNING_COMPLETE_BONUS
                : PointRewardType.EVENING_COMPLETE_BONUS;
    }
}
