package oba.backend.server.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oba.backend.server.common.jwt.JwtProvider;
import oba.backend.server.dto.TokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // authentication 기반으로 JWT 만들기
        TokenResponse tokens = jwtProvider.generateToken(authentication);

        // Refresh Token 쿠키 설정
        Cookie refreshCookie = new Cookie("refresh_token", tokens.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        refreshCookie.setAttribute("SameSite", "None");
        response.addCookie(refreshCookie);

        // Access Token 쿠키 설정
        Cookie accessCookie = new Cookie("access_token", tokens.accessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 60);
        accessCookie.setAttribute("SameSite", "None");
        response.addCookie(accessCookie);

        // 로그인 성공 페이지로 이동
        response.sendRedirect("/login?success=true");
    }
}
