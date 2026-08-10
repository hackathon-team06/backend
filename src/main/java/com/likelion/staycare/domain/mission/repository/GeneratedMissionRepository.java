package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.enums.MissionTime;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GeneratedMissionRepository extends JpaRepository<GeneratedMission, Long> {

    Optional<GeneratedMission> findByUserAndMissionDateAndMissionTime(
            User user,
            LocalDate missionDate,
            MissionTime missionTime
    );

    List<GeneratedMission> findAllByUserAndMissionDateOrderByMissionTimeAsc(
            User user,
            LocalDate missionDate
    );
}
