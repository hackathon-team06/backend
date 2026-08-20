package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.DailySkinCheck;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailySkinCheckRepository extends JpaRepository<DailySkinCheck, Long> {

    Optional<DailySkinCheck> findByUserAndCheckedDate(User user, LocalDate checkedDate);
}
