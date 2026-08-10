    package com.likelion.staycare.domain.schedule.entity;

    import com.likelion.staycare.domain.schedule.entity.enums.Companion;
    import com.likelion.staycare.domain.schedule.entity.enums.ScheduleCategory;
    import com.likelion.staycare.domain.user.entity.User;
    import com.likelion.staycare.global.common.BaseTimeEntity;
    import jakarta.persistence.AttributeOverride;
    import jakarta.persistence.AttributeOverrides;
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

    import java.time.LocalDate;

    @Entity
    @Getter
    @Table(
            name = "schedules",
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_schedule_user_date",
                    columnNames = {"user_id", "schedule_date"}
            )
    )
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AttributeOverrides({
            @AttributeOverride(name = "createdAt", column = @Column(name = "created_at")),
            @AttributeOverride(name = "modifiedAt", column = @Column(name = "modified_at"))
    })
    public class Schedule extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "schedule_id")
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(name = "schedule_date", nullable = false)
        private LocalDate scheduleDate;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private Companion companion;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private ScheduleCategory category;

        @Builder
        public Schedule(User user, LocalDate scheduleDate, Companion companion, ScheduleCategory category) {
            this.user = user;
            this.scheduleDate = scheduleDate;
            this.companion = companion;
            this.category = category;
        }

        public void updateSchedule(Companion companion, ScheduleCategory category) {
            this.companion = companion;
            this.category = category;
        }
    }
