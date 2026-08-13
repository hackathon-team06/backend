package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningRoutine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MorningRoutineRepository extends JpaRepository<MorningRoutine, Long> {

    Optional<MorningRoutine> findByUserId(Long userId);
}
