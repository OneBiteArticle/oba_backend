package oba.backend.server.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import oba.backend.server.dto.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;


// JWT 토큰을 생성하고, 검증하고, 인증 정보를 추출
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenValidity;   // AccessToken 유효기간
    private final long refreshTokenValidity;  // RefreshToken 유효기간

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration-ms:1800000}") long accessTokenValidity,
            @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshTokenValidity
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    // 토큰 생성 (Access, Refresh 동시 발급)
    public TokenResponse generateToken(Authentication authentication) {
        String userId = ((User) authentication.getPrincipal()).getUsername();
        String accessToken = createToken(userId, "access", accessTokenValidity);
        String refreshToken = createToken(userId, "refresh", refreshTokenValidity);
        return new TokenResponse(accessToken, refreshToken);
    }

    private String createToken(String subject, String type, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", type)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰을 검증
    public boolean validateToken(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 토큰에서 인증 정보를 추출하고, 이를 Spring Security의 Authentication 객체로 변환하여 반환
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        User principal = new User(username, "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // Claims 가져오기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
