package oba.backend.server.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ✅ OAuth2FailureHandler
 * - OAuth2 로그인(Google, Kakao, Naver 등) 과정에서 예외가 발생했을 때 실행되는 핸들러
 * - Spring Security의 AuthenticationFailureHandler 인터페이스를 구현함
 * - 주로 로그인 실패 시 사용자를 /login 페이지로 리다이렉트하거나, 에러 메시지를 전달하는 데 사용
 */
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    /**
     * ✅ onAuthenticationFailure()
     * - OAuth2 인증(로그인) 실패 시 자동으로 호출되는 메서드
     * - AuthenticationException에 실패 원인이 담겨 있음 (토큰 오류, 요청 만료, 거절 등)
     *
     * @param request  로그인 요청 객체 (ex: /oauth2/authorization/google)
     * @param response 응답 객체 (리다이렉트 등 응답 조작 가능)
     * @param exception 로그인 실패 원인을 담은 예외 객체
     */
    @Override
    public void onAuthenticationFailure(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        // ❌ 민감한 에러 내용을 그대로 사용자에게 노출하지 않기 위해
        // exception.getMessage() 대신 "oauth2_login_failed" 라는 고정된 코드만 전달함
        String msg = URLEncoder.encode("oauth2_login_failed", StandardCharsets.UTF_8);

        // ⚙️ 로그인 실패 시 로그인 페이지로 리다이렉트
        // 사용자는 /login?error=oauth2_login_failed 로 이동하게 됨
        response.sendRedirect("/login?error=" + msg);

        // 🪵 서버 내부 로그로는 예외의 상세 정보를 남길 수 있음 (사용자에게는 비공개)
        // log.warn("OAuth2 login failed", exception);
    }
}
