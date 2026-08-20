package com.likelion.staycare.domain.point.repository;

import com.likelion.staycare.domain.point.entity.PointHistory;
import com.likelion.staycare.domain.point.entity.PointRewardType;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    boolean existsByUserIdAndStepIdAndRewardType(Long userId, Long stepId, PointRewardType rewardType);


    boolean existsByUserIdAndRewardTypeAndRewardKey(
            Long userId,
            PointRewardType rewardType,
            String rewardKey
    );

    List<PointHistory> findAllByUserAndRewardTypeAndCreatedAtBetween(
            User user,
            PointRewardType rewardType,
            LocalDateTime start,
            LocalDateTime end
    );

}
