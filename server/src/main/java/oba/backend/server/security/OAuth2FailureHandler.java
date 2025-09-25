package oba.backend.server.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            jakarta.servlet.http.HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        // 민감한 exception.getMessage() 대신 고정 코드 전달
        String msg = URLEncoder.encode("oauth2_login_failed", StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=" + msg);

        // 실제 상세 에러는 서버 로그에만 기록
        // log.warn("OAuth2 login failed", exception);
    }
}
