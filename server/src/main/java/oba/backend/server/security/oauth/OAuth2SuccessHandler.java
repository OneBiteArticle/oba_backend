package oba.backend.server.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oba.backend.server.security.jwt.JwtTokenProvider;
import oba.backend.server.security.oauth.dto.CustomOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(oAuthUser.getUserId());

        // 앱에서 전달한 redirect_uri 받기
        String redirectUri = request.getParameter("redirect_uri");

        // 없다면 기본값
        if (redirectUri == null) redirectUri = "myapp://oauth";

        String targetUrl = redirectUri + "?token=" + jwt;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
