package com.likelion.staycare.domain.stamp.repository;

import com.likelion.staycare.domain.stamp.entity.StampRewardPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StampRewardPolicyRepository extends JpaRepository<StampRewardPolicy, Long> {

    Optional<StampRewardPolicy> findByPeriodDaysAndCompletedDays(Integer periodDays, Integer completedDays);
}
