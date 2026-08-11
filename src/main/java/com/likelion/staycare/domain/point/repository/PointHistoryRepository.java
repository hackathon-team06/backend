package com.likelion.staycare.domain.point.repository;

import com.likelion.staycare.domain.point.entity.PointHistory;
import com.likelion.staycare.domain.point.entity.PointRewardType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    boolean existsByUserIdAndStepIdAndRewardType(Long userId, Long stepId, PointRewardType rewardType);

    boolean existsByUserIdAndMissionIdAndRewardType(Long userId, Long missionId, PointRewardType rewardType);
}
