package com.likelion.staycare.domain.googlecalendar.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "google_calendar_connections",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_google_calendar_connection_user",
                        columnNames = "user_id"
                )
        }
)
public class GoogleCalendarConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "google_calendar_connection_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Lob
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "token_type", length = 50)
    private String tokenType;

    @Column(name = "scope", length = 500)
    private String scope;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "connected", nullable = false)
    private boolean connected;

    @Builder
    public GoogleCalendarConnection(
            User user,
            String accessToken,
            String refreshToken,
            String tokenType,
            String scope,
            LocalDateTime tokenExpiresAt,
            boolean connected
    ) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.tokenExpiresAt = tokenExpiresAt;
        this.connected = connected;
    }

    public void updateTokens(
            String accessToken,
            String refreshToken,
            String tokenType,
            String scope,
            LocalDateTime tokenExpiresAt
    ) {
        this.accessToken = accessToken;
        if (refreshToken != null && !refreshToken.isBlank()) {
            this.refreshToken = refreshToken;
        }
        this.tokenType = tokenType;
        this.scope = scope;
        this.tokenExpiresAt = tokenExpiresAt;
        this.connected = true;
    }
}
