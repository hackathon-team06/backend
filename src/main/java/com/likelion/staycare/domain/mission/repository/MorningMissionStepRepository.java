package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningMission;
import com.likelion.staycare.domain.mission.entity.MorningMissionStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MorningMissionStepRepository extends JpaRepository<MorningMissionStep, Long> {

    List<MorningMissionStep> findByMorningMissionOrderByStepOrderAsc(MorningMission mission);
}
