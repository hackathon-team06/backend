package com.likelion.staycare.domain.mission.repository;

import com.likelion.staycare.domain.mission.entity.GeneratedMission;
import com.likelion.staycare.domain.mission.entity.GeneratedMissionStep;
import com.likelion.staycare.domain.mission.entity.UserMissionStepCheck;
import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMissionStepCheckRepository extends JpaRepository<UserMissionStepCheck, Long> {

    List<UserMissionStepCheck> findByGeneratedMissionStepGeneratedMission(GeneratedMission mission);

    Optional<UserMissionStepCheck> findByGeneratedMissionStepId(Long stepId);

    void deleteAllByGeneratedMissionStepIn(List<GeneratedMissionStep> steps);

    void deleteByGeneratedMissionStepId(Long stepId);

    @Query("""
        select c.generatedMissionStep.id
        from UserMissionStepCheck c
        where c.generatedMissionStep in :steps
          and c.checked = true
    """)
    List<Long> findCheckedStepIds(@Param("steps") List<GeneratedMissionStep> steps);

}
