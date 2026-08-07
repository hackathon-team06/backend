package com.likelion.staycare.domain.notification.repository;

import com.likelion.staycare.domain.notification.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByUserId(Long userId);
}
