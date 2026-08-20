package com.likelion.staycare.domain.user.repository;

import com.likelion.staycare.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    List<User> findAllByNotificationEnabledTrueAndReturnHomeTime(LocalTime returnHomeTime);
}
