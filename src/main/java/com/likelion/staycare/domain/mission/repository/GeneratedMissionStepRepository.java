package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedMissionStepRepository extends JpaRepository<GeneratedMissionStep, Long> {

    List<GeneratedMissionStep> findByGeneratedMissionOrderByStepOrderAsc(GeneratedMission mission);

    List<GeneratedMissionStep> findByGeneratedMissionAndStepOrderGreaterThanOrderByStepOrderAsc(
            GeneratedMission mission,
            int stepOrder
    );
}
