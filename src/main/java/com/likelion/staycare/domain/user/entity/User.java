package com.likelion.staycare.domain.user.entity;

import com.likelion.staycare.domain.diagnosis.entity.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String loginId;

    @Column(nullable = false)
    private String password;

    // === 마이페이지에서 편집 가능한 필드 ===
    @Column(length = 20)
    private String nickname;

    @Column(length = 100)
    private String goal;

    // === 자가진단으로만 갱신되는 필드 ===
    @Enumerated(EnumType.STRING)
    private AgeRange ageRange;

    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    private SleepHours sleepHours;

    @Enumerated(EnumType.STRING)
    private ReturnHomeTime returnHomeTime;

    @Enumerated(EnumType.STRING)
    private CheckCycle checkCycle;

    @Enumerated(EnumType.STRING)
    private CareMotivation careMotivation;

    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @Column(name = "last_notified_date")
    private LocalDate lastNotifiedDate;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "profile_image_key")
    private String profileImageKey;


    @Builder
    public User(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.notificationEnabled = true;
    }

    // === 마이페이지 수정 메서드 ===
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateGoal(String goal) {
        this.goal = goal;

    }

    public void updateNotificationSetting(Boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public void updateProfileImage(String profileImageUrl, String profileImageKey) {
        this.profileImageUrl = profileImageUrl;
        this.profileImageKey = profileImageKey;
    }

    public void removeProfileImage() {
        this.profileImageUrl = null;
        this.profileImageKey = null;
    }


    // === 자가진단 결과 반영 메서드 ===
    public void applyDiagnosisResult(
            AgeRange ageRange,
            SkinType skinType,
            SleepHours sleepHours,
            ReturnHomeTime returnHomeTime,
            CheckCycle checkCycle,
            CareMotivation careMotivation
    ) {
        this.ageRange = ageRange;
        this.skinType = skinType;
        this.sleepHours = sleepHours;
        this.returnHomeTime = returnHomeTime;
        this.checkCycle = checkCycle;
        this.careMotivation = careMotivation;
    }

    public void markNotified(LocalDate date) {
        this.lastNotifiedDate = date;
    }

    public boolean hasDiagnosis() {
        return this.skinType != null;
    }
}
