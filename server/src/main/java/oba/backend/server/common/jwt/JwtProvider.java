package oba.backend.server.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import oba.backend.server.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;

    @Value("${jwt.access-token-expiration-ms:1800000}")
    private long accessTokenExpire;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpire;

    /**
     * application.yml → jwt.secret 값 주입
     * (.env는 application.yml에서 import: optional:file:config-env.properties 로 읽는다)
     */
    public JwtProvider(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Authentication → JWT 생성
     */
    public TokenResponse generateToken(Authentication authentication) {
        String identifier = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        long now = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setSubject(identifier)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTokenExpire))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(identifier)
                .claim("type", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenExpire))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * Claims 가져오기
     */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 유효성 확인
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.error("[JWT] Token invalid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT → Authentication 변환
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        String identifier = claims.getSubject();
        String role = claims.get("role", String.class);

        return new UsernamePasswordAuthenticationToken(
                identifier,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
