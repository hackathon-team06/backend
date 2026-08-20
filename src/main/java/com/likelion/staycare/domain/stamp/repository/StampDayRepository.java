package com.likelion.staycare.domain.stamp.repository;

import com.likelion.staycare.domain.stamp.entity.StampDay;
import com.likelion.staycare.domain.stamp.entity.StampDailyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.time.LocalDate;

public interface StampDayRepository extends JpaRepository<StampDay, Long> {

    Optional<StampDay> findByStampBookIdAndTargetDate(Long stampBookId, LocalDate targetDate);

    @Query("""
            select count(sd)
            from StampDay sd
            where sd.stampBook.id = :stampBookId
              and sd.status in :statuses
            """)
    int countProgressDays(Long stampBookId, Collection<StampDailyStatus> statuses);
}
