package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserMissionStepCheckRepository extends JpaRepository<UserMissionStepCheck, Long> {

    List<UserMissionStepCheck> findByGeneratedMissionStepGeneratedMission(GeneratedMission mission);
}
