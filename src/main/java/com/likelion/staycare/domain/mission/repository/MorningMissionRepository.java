package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.diagnosis.entity.SkinType;
import com.likelion.staycare.domain.mission.entity.MorningMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MorningMissionRepository extends JpaRepository<MorningMission, Long> {

    List<MorningMission> findBySkinTypeAndActiveTrue(SkinType skinType);
}
