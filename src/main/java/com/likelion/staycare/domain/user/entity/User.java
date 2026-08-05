package com.likelion.staycare.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Column(length = 20)
    private String nickname;

    @Column(length = 100)
    private String goal;

    @Builder
    public User(String loginId, String password, String nickname, String goal) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.goal = goal;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateGoal(String goal) {
        this.goal = goal;
    }
}
