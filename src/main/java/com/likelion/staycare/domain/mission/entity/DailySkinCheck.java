package com.likelion.staycare.domain.mission.entity;

import com.likelion.staycare.domain.mission.entity.enums.SkinCondition;
import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "daily_skin_checks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_daily_skin_check_user_date",
                columnNames = {"user_id", "checked_date"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySkinCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skin_check_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_condition", nullable = false, length = 30)
    private SkinCondition skinCondition;

    @Column(name = "checked_date", nullable = false)
    private LocalDate checkedDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public DailySkinCheck(User user, SkinCondition skinCondition, LocalDate checkedDate) {
        this.user = user;
        this.skinCondition = skinCondition;
        this.checkedDate = checkedDate;
    }
}
