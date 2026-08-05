package com.likelion.staycare.global.security;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더에서 Access Token 추출
        String token = resolveToken(request);

        try {
            // 토큰이 존재하고 유효한 경우
            if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {

                // 토큰에서 userId 추출 (DB 조회 없이 토큰 값만 사용)
                Long userId = jwtProvider.getUserId(token);

                // userId 기반으로 경량 UserDetails 생성
                CustomUserDetails userDetails = new CustomUserDetails(userId);

                // Spring Security가 사용할 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                // 현재 요청의 인증 정보를 SecurityContext에 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 토큰이 잘못되었거나 인증 처리 중 예외 발생 시 인증 정보 제거
            log.warn("JWT 인증 처리 실패: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
