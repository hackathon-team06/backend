package com.likelion.staycare.domain.notification.entity;

import com.likelion.staycare.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "push_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 500)
    private String token;

    @Column(nullable = false)
    private Boolean active = true;

    @Builder
    public PushToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.active = true;
    }

    public void updateToken(String token) {
        this.token = token;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
