package com.likelion.staycare.domain.diagnosis.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "diagnoses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeRange ageRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SleepHours sleepHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnHomeTime returnHomeTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckCycle checkCycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareMotivation careMotivation;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @CreatedDate
    private LocalDateTime diagnosedAt;

    @Builder
    public Diagnosis(User user, AgeRange ageRange, SleepHours sleepHours,
                     SkinType skinType, ReturnHomeTime returnHomeTime,
                     CheckCycle checkCycle, CareMotivation careMotivation,
                     String recommendation) {
        this.user = user;
        this.ageRange = ageRange;
        this.sleepHours = sleepHours;
        this.skinType = skinType;
        this.returnHomeTime = returnHomeTime;
        this.checkCycle = checkCycle;
        this.careMotivation = careMotivation;
        this.recommendation = recommendation;
    }
}
