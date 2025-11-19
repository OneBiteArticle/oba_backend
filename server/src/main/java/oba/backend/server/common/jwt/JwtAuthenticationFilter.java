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

        // 1. Access Token 가져오기
        String token = resolveTokenFromCookies(request);

        // 토큰이 존재하고 유효한 경우만 인증 처리
        if (token != null && jwtProvider.validateToken(token)) {

            // 2. Refresh Token은 인증 불가 → 바로 다음 필터로
            // RefreshToken은 access_token이 아닌 refresh_token 쿠키에 저장됨
            if (isRefreshToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Access Token이면 인증 객체 생성
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return false;

        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> "refresh_token".equals(cookie.getName()));
    }

    private String resolveTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "access_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
