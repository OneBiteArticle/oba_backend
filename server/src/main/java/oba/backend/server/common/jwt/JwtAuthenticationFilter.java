package oba.backend.server.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // ✅ 1. Access Token 먼저 꺼내오기 (쿠키에서)
        String token = resolveTokenFromCookies(request);

        if (token != null && jwtProvider.validateToken(token)) {
            var claims = jwtProvider.getClaims(token);

            // ✅ 2. Refresh Token이면 인증 불가 → 그냥 다음 필터로
            if ("refresh".equals(claims.get("type"))) {
                filterChain.doFilter(request, response);
                return;
            }

            // ✅ 3. Access Token이면 SecurityContext에 인증정보 저장
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * AccessToken을 HttpOnly 쿠키에서 가져오기
     */
    private String resolveTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
