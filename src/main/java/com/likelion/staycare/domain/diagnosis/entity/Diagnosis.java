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
import java.time.LocalTime;

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

    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Integer age;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinType skinType;

    @Column
    private LocalTime wakeUpTime;

    @Column
    private LocalTime returnHomeTime;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckCycle checkCycle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareMotivation careMotivation;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @CreatedDate
    @Column(name = "diagnosed_at", nullable = false, updatable = false)
    private LocalDateTime diagnosedAt;

    @PrePersist
    protected void onCreate() {
        if (this.diagnosedAt == null) {
            this.diagnosedAt = LocalDateTime.now();
        }
    }



    @Builder
    public Diagnosis(User user, Gender gender, Integer age,
                     SkinType skinType, LocalTime wakeUpTime, LocalTime returnHomeTime,
                     CheckCycle checkCycle, CareMotivation careMotivation,
                     String recommendation) {
        this.user = user;
        this.gender = gender;
        this.age = age;
        this.skinType = skinType;
        this.wakeUpTime = wakeUpTime;
        this.returnHomeTime = returnHomeTime;
        this.checkCycle = checkCycle;
        this.careMotivation = careMotivation;
        this.recommendation = recommendation;
    }

}
