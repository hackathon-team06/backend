package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningRoutine;
import com.likelion.staycare.domain.mission.entity.MorningRoutineItem;
import com.likelion.staycare.domain.mission.entity.enums.MorningRoutineItemSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MorningRoutineItemRepository extends JpaRepository<MorningRoutineItem, Long> {

    List<MorningRoutineItem> findByMorningRoutineOrderByItemOrderAsc(MorningRoutine morningRoutine);

    List<MorningRoutineItem> findByMorningRoutineAndSourceOrderByItemOrderAsc(
            MorningRoutine morningRoutine,
            MorningRoutineItemSource source
    );

    List<MorningRoutineItem> findByMorningRoutineAndSourceInOrderByItemOrderAsc(
            MorningRoutine morningRoutine,
            Collection<MorningRoutineItemSource> sources
    );

    void deleteByMorningRoutine(MorningRoutine morningRoutine);

    void deleteByMorningRoutineAndSource(MorningRoutine morningRoutine, MorningRoutineItemSource source);

    void deleteByMorningRoutineAndSourceIn(MorningRoutine morningRoutine, Collection<MorningRoutineItemSource> sources);
}
