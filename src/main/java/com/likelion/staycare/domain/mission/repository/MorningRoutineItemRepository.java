package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningRoutine;
import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MorningRoutineItemRepository extends JpaRepository<MorningRoutineItem, Long> {

    List<MorningRoutineItem> findByMorningRoutineOrderByItemOrderAsc(MorningRoutine morningRoutine);

    void deleteByMorningRoutine(MorningRoutine morningRoutine);
}
