package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface GeneratedMissionRepository extends JpaRepository<GeneratedMission, Long> {

    Optional<GeneratedMission> findByUserAndMissionDate(User user, LocalDate missionDate);
}
