package oba.backend.server.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final String secretBase64;
    private Key key;
    private final long accessValidityMs;
    private final long refreshValidityMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.access-token-expiration-ms:1800000}") long accessValidityMs,      // 30분
            @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshValidityMs   // 7일
    ) {
        this.secretBase64 = secretBase64;
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    /**
     * 초기화 시점에 키 검증 및 생성
     */
    @PostConstruct
    private void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretBase64);

        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be at least 256 bits (32 bytes) after Base64 decoding");
        }

        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, accessValidityMs);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(String subject) {
        return buildToken(subject, Map.of("type", "refresh"), refreshValidityMs);
    }

    /**
     * 토큰 생성 공통 메서드
     */
    private String buildToken(String subject, Map<String, Object> claims, long validity) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validity))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 필요시 로거 사용 가능: log.warn("Invalid JWT token", e);
            return false;
        }
    }

    /**
     * 토큰 Claims 반환
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰 Subject(주체: 보통 user email/ID) 반환
     */
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
