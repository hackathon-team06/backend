package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.UserMorningStepCheck;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserMorningStepCheckRepository extends JpaRepository<UserMorningStepCheck, Long> {

    List<UserMorningStepCheck> findByUserAndMissionDate(User user, LocalDate missionDate);
}
