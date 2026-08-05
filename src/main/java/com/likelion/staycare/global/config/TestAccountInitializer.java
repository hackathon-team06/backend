package com.likelion.staycare.global.config;

import com.likelion.staycare.domain.user.entity.User;
import com.likelion.staycare.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String TEST_LOGIN_ID = "test";
    private static final String TEST_PASSWORD = "1234";

    @Override
    public void run(String... args) {
        if (userRepository.existsByLoginId(TEST_LOGIN_ID)) {
            log.info("✅ 테스트 계정이 이미 존재합니다. (loginId: {})", TEST_LOGIN_ID);
            return;
        }

        User testUser = User.builder()
                .loginId(TEST_LOGIN_ID)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .build();

        userRepository.save(testUser);

        log.info("🎉 테스트 계정이 생성되었습니다!");
        log.info("   ├─ ID: {}", TEST_LOGIN_ID);
        log.info("   ├─ Password: {}", TEST_PASSWORD);
    }
}
