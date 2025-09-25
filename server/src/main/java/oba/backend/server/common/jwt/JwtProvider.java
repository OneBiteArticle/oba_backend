package oba.backend.server.common.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import oba.backend.server.dto.TokenResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long accessTokenValidity = 1000 * 60 * 30; // 30분
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7; // 7일

    // ✅ 토큰 생성
    public TokenResponse generateToken(Authentication authentication) {
        String accessToken = createToken(authentication.getName(), "access", accessTokenValidity);
        String refreshToken = createToken(authentication.getName(), "refresh", refreshTokenValidity);
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
                .signWith(key)
                .compact();
    }

    // ✅ 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ Authentication 추출
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        String username = claims.getSubject();
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        User principal = new User(username, "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // ✅ Claims 가져오기
    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }
}
