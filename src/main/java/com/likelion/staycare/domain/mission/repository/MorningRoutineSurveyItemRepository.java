package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.MorningRoutineSurvey;
import com.likelion.staycare.domain.mission.entity.MorningRoutineSurveyItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MorningRoutineSurveyItemRepository extends JpaRepository<MorningRoutineSurveyItem, Long> {

    List<MorningRoutineSurveyItem> findByMorningRoutineSurveyOrderByItemOrderAsc(MorningRoutineSurvey morningRoutineSurvey);
}
