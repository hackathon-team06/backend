package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningRoutineSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MorningRoutineSurveyRepository extends JpaRepository<MorningRoutineSurvey, Long> {

    Optional<MorningRoutineSurvey> findByUserId(Long userId);
}
