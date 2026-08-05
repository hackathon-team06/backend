package com.likelion.staycare.global.security;

import java.util.Collection;
import java.util.Collections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    // JWT에서 추출한 userId만 보관 (DB 조회 없이 경량 인증 객체 생성)
    private final Long userId;

    // 권한 목록 반환: 테스트 계정은 모두 ROLE_USER
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 비밀번호 미사용
    @Override
    public String getPassword() {
        return null;
    }

    // 사용자 식별값: userId를 문자열로 반환
    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
