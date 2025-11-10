package oba.backend.server.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oba.backend.server.common.jwt.JwtProvider;
import oba.backend.server.dto.TokenResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * ✅ OAuth2SuccessHandler
 * - OAuth2 (Google, Kakao, Naver 등) 로그인 성공 시 실행되는 커스텀 핸들러
 * - 로그인 성공 후, 사용자를 위한 JWT Access Token / Refresh Token을 발급하고
 *   이를 안전하게 쿠키(HttpOnly)에 저장한 뒤 /login?success=true 로 리다이렉트함
 */
@RequiredArgsConstructor // JwtProvider 의존성 자동 주입
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider; // ✅ JWT 생성 및 검증 담당 클래스

    /**
     * ✅ OAuth2 인증 성공 시 호출되는 메서드
     *
     * @param request       로그인 요청 (ex: /oauth2/authorization/google)
     * @param response      서버 응답 객체 (쿠키 추가 / 리다이렉트 처리)
     * @param authentication Spring Security가 인증 완료 후 생성한 인증 객체
     *
     * 동작 순서:
     *  1️⃣ 로그인 성공 시 JwtProvider를 이용해 AccessToken / RefreshToken 발급
     *  2️⃣ 두 토큰을 HttpOnly 쿠키에 저장 (XSS 방어)
     *  3️⃣ 로그인 성공 페이지(/login?success=true)로 리다이렉트
     */
    @Override
    public void onAuthenticationSuccess(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        // ✅ 1️⃣ JWT Access / Refresh 토큰 생성
        // authentication 객체에는 로그인한 사용자의 인증 정보가 담겨 있음
        TokenResponse tokens = jwtProvider.generateToken(authentication);

        // ----------------------------------------------------------------------
        // 🧩 2️⃣ Refresh Token 설정 (7일 유지)
        // - 브라우저가 자동으로 전송하도록 HttpOnly 쿠키로 저장
        // - Secure, SameSite=None → HTTPS 환경에서만 사용, 크로스도메인 가능
        // ----------------------------------------------------------------------
        Cookie refreshCookie = new Cookie("refresh_token", tokens.refreshToken());
        refreshCookie.setHttpOnly(true);                  // JS 접근 차단 (보안)
        refreshCookie.setSecure(true);                    // HTTPS에서만 전송
        refreshCookie.setPath("/");                       // 모든 경로에 대해 유효
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);        // 유효기간 7일

//        이 부분은 JWT 쿠키가 “다른 도메인(=CORS 환경)”에서도 잘 전달되도록 설정하는 핵심 옵션이에요.
//        즉, 프론트엔드가 localhost:3000이고, 백엔드가 localhost:8080이라면,
//                두 도메인이 달라서 기본적으로 쿠키가 전송되지 않아요.
//        이때 SameSite=None 설정이 꼭 필요합니다.
        refreshCookie.setAttribute("SameSite", "None");   // CORS 환경에서도 쿠키 전달 허용
        response.addCookie(refreshCookie);

        // ----------------------------------------------------------------------
        // 🧩 3️⃣ Access Token 설정 (30분 유지)
        // - 요청 시 인증에 사용되는 JWT (짧은 수명)
        // ----------------------------------------------------------------------
        Cookie accessCookie = new Cookie("access_token", tokens.accessToken());
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(30 * 60);                  // 30분
        accessCookie.setAttribute("SameSite", "None");
        response.addCookie(accessCookie);

        // ----------------------------------------------------------------------
        // 🧩 4️⃣ 로그인 성공 후 리다이렉트
        // - 토큰은 쿠키에 이미 저장되었기 때문에, URL로 전달하지 않음
        // - 클라이언트는 "/login?success=true" 를 보고 성공 여부 판단 가능
        // ----------------------------------------------------------------------
        response.sendRedirect("/login?success=true");
    }
}
