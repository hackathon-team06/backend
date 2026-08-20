package com.likelion.staycare.domain.stamp.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(
        name = "stamp_days",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stamp_day_book_date",
                        columnNames = {"stamp_book_id", "target_date"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StampDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_day_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stamp_book_id", nullable = false)
    private StampBook stampBook;

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StampDailyStatus status;

    @Column(nullable = false)
    private Integer completedStepCount;

    @Column(nullable = false)
    private Integer totalStepCount;

    @Column(nullable = false)
    private Integer dailyPoint;

    @Builder
    public StampDay(
            StampBook stampBook,
            LocalDate targetDate,
            StampDailyStatus status,
            Integer completedStepCount,
            Integer totalStepCount,
            Integer dailyPoint
    ) {
        this.stampBook = stampBook;
        this.targetDate = targetDate;
        this.status = status;
        this.completedStepCount = completedStepCount;
        this.totalStepCount = totalStepCount;
        this.dailyPoint = dailyPoint;
    }

    public void update(
            StampDailyStatus status,
            int completedStepCount,
            int totalStepCount,
            int dailyPoint
    ) {
        this.status = status;
        this.completedStepCount = completedStepCount;
        this.totalStepCount = totalStepCount;
        this.dailyPoint = dailyPoint;
    }
}
