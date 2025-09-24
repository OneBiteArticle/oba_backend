package oba.backend.server.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtProvider {

    private final Key key;
    private final long accessValidityMs;
    private final long refreshValidityMs;

    public JwtProvider(
            @Value("${jwt.secret}") String secretBase64,
            @Value("${jwt.access-token-expiration-ms:1800000}") long accessValidityMs,      // 30분
            @Value("${jwt.refresh-token-expiration-ms:604800000}") long refreshValidityMs   // 7일
    ) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretBase64));
        this.accessValidityMs = accessValidityMs;
        this.refreshValidityMs = refreshValidityMs;
    }

    public String createAccessToken(String subject, Map<String, Object> claims) {
        return buildToken(subject, claims, accessValidityMs);
    }

    public String createRefreshToken(String subject) {
        return buildToken(subject, Map.of("type", "refresh"), refreshValidityMs);
    }

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

    public boolean validate(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
