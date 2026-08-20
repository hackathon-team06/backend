package com.likelion.staycare.domain.stamp.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "stamp_books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StampBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stamp_book_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer periodDays; // 7, 14, 21, 28

    @Column(nullable = false)
    private Integer completedDays = 0;

    @Column(nullable = false)
    private Integer completionPointAwarded = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StampBookStatus status = StampBookStatus.IN_PROGRESS;

    private LocalDateTime settledAt;

    @Builder
    public StampBook(
            User user,
            LocalDate startDate,
            LocalDate endDate,
            Integer periodDays
    ) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.periodDays = periodDays;
        this.completedDays = 0;
        this.completionPointAwarded = 0;
        this.status = status;
    }

    public void settle(int completedDays, int completionPointAwarded, LocalDateTime settledAt) {
        this.completedDays = completedDays;
        this.completionPointAwarded = completionPointAwarded;
        this.settledAt = settledAt;
        this.status = StampBookStatus.SETTLED;
    }
}
