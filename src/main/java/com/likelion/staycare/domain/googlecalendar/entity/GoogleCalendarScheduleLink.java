package com.likelion.staycare.domain.googlecalendar.entity;

import com.likelion.staycare.domain.schedule.entity.Schedule;
import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "google_calendar_schedule_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_google_calendar_schedule_link_user_event",
                        columnNames = {"user_id", "google_event_id"}
                )
        }
)
public class GoogleCalendarScheduleLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "google_calendar_schedule_link_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "schedule_id", nullable = false, unique = true)
    private Schedule schedule;

    @Column(name = "google_calendar_id", nullable = false, length = 100)
    private String googleCalendarId;

    @Column(name = "google_event_id", nullable = false, length = 255)
    private String googleEventId;

    @Column(name = "last_synced_at", nullable = false)
    private LocalDateTime lastSyncedAt;

    @Builder
    public GoogleCalendarScheduleLink(
            User user,
            Schedule schedule,
            String googleCalendarId,
            String googleEventId,
            LocalDateTime lastSyncedAt
    ) {
        this.user = user;
        this.schedule = schedule;
        this.googleCalendarId = googleCalendarId;
        this.googleEventId = googleEventId;
        this.lastSyncedAt = lastSyncedAt;
    }

    public void touchSyncedAt() {
        this.lastSyncedAt = LocalDateTime.now();
    }
}
