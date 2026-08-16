package com.likelion.staycare.domain.point.service;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.point.dto.response.PointResponse;
import com.likelion.staycare.domain.point.entity.PointHistory;
import com.likelion.staycare.domain.point.entity.PointRewardType;
import com.likelion.staycare.domain.point.entity.PointWallet;
import com.likelion.staycare.domain.point.exception.PointErrorCode;
import com.likelion.staycare.domain.point.repository.PointHistoryRepository;
import com.likelion.staycare.domain.point.repository.PointWalletRepository;
import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.exception.UserErrorCode;
import com.likelion.staycare.domain.user.repository.UserRepository;
import com.likelion.staycare.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private static final int STEP_POINT = 1;
    private static final int DIAGNOSIS_REWARD_POINT = 10;
    private static final String DIAGNOSIS_REWARD_KEY = "ONBOARDING_DIAGNOSIS";

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
        try {
            if (pointHistoryRepository.existsByUserIdAndStepIdAndRewardType(
                    user.getId(),
                    step.getId(),
                    PointRewardType.MISSION_STEP
            )) {
                return;
            }

            pointHistoryRepository.saveAndFlush(
                    PointHistory.builder()
                            .user(user)
                            .mission(step.getGeneratedMission())
                            .step(step)
                            .rewardType(PointRewardType.MISSION_STEP)
                            .rewardKey(null)
                            .amount(STEP_POINT)
                            .build()
            );

            PointWallet pointWallet = getOrCreatePointWallet(user);
            pointWallet.addPoint(STEP_POINT);
            pointWalletRepository.saveAndFlush(pointWallet);
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateRewardException(e)) {
                return;
            }
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        } catch (DataAccessException e) {
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        }
    }

    @Transactional
    public int rewardDiagnosisComplete(User user) {
        try {
            if (pointHistoryRepository.existsByUserIdAndRewardTypeAndRewardKey(
                    user.getId(),
                    PointRewardType.DIAGNOSIS_COMPLETE,
                    DIAGNOSIS_REWARD_KEY
            )) {
                return 0;
            }

            pointHistoryRepository.saveAndFlush(
                    PointHistory.builder()
                            .user(user)
                            .mission(null)
                            .step(null)
                            .rewardType(PointRewardType.DIAGNOSIS_COMPLETE)
                            .rewardKey(DIAGNOSIS_REWARD_KEY)
                            .amount(DIAGNOSIS_REWARD_POINT)
                            .build()
            );

            PointWallet pointWallet = getOrCreatePointWallet(user);
            pointWallet.addPoint(DIAGNOSIS_REWARD_POINT);
            pointWalletRepository.saveAndFlush(pointWallet);

            return DIAGNOSIS_REWARD_POINT;
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateRewardException(e)) {
                return 0;
            }
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        } catch (DataAccessException e) {
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        }
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }
    }

    private PointWallet getOrCreatePointWallet(User user) {
        try {
            return pointWalletRepository.findByUserId(user.getId())
                    .orElseGet(() -> createPointWallet(user));
        } catch (DataAccessException e) {
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        }
    }

    private PointWallet createPointWallet(User user) {
        try {
            return pointWalletRepository.saveAndFlush(
                    PointWallet.builder()
                            .user(user)
                            .point(0)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            if (isPointWalletDuplicateException(e)) {
                return pointWalletRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new CustomException(PointErrorCode.POINT_PROCESS_FAILED));
            }
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        } catch (DataAccessException e) {
            throw new CustomException(PointErrorCode.POINT_PROCESS_FAILED);
        }
    }

    private boolean isDuplicateRewardException(DataIntegrityViolationException e) {
        String message = extractMessage(e);
        return message.contains("uk_point_history_user_step_reward")
                || message.contains("uk_point_history_user_mission_reward")
                || message.contains("uk_point_history_user_reward_key");
    }

    private boolean isPointWalletDuplicateException(DataIntegrityViolationException e) {
        return extractMessage(e).contains("uk_point_wallet_user");
    }

    private String extractMessage(Exception e) {
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            return e.getCause().getMessage();
        }
        return e.getMessage() == null ? "" : e.getMessage();
    }
}
