package com.likelion.staycare.global.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String TOKEN_TYPE = "ACCESS_TOKEN";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration) {

        // JWT 서명에 사용할 SecretKey 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Access Token 만료 시간
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * userId 기반으로 Access Token 생성
     * (로그인 성공 시 서비스 계층에서 호출)
     */
    public String createAccessToken(Long userId) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userId))       // 토큰 주인 식별값 (userId)
                .claim("type", TOKEN_TYPE)             // 토큰 타입
                .claim("roles", List.of("ROLE_USER"))  // 사용자 권한 (테스트 계정 기본값)
                .issuedAt(now)                         // 발급 시간
                .expiration(expiredAt)                 // 만료 시간
                .signWith(secretKey)                   // 서명
                .compact();
    }

    /**
     * 이미 인증된 CustomUserDetails 기반으로 토큰 생성 (선택적 사용)
     */
    public String createAccessToken(CustomUserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(String.valueOf(userDetails.getUserId()))
                .claim("type", TOKEN_TYPE)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiredAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰 유효성 검증 (서명, 만료시간, 타입 확인)
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String type = claims.get("type", String.class);
            return TOKEN_TYPE.equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 userId 추출
     */
    public Long getUserId(String token) {
        return Long.valueOf(extractClaims(token).getSubject());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
