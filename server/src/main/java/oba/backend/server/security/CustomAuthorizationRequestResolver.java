package oba.backend.server.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;

    private final String authorizationRequestBaseUri = "/oauth2/authorization";

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        DefaultOAuth2AuthorizationRequestResolver baseResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        OAuth2AuthorizationRequest req = baseResolver.resolve(request);
        if (req == null) return null;
        return customize(request, req);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        DefaultOAuth2AuthorizationRequestResolver baseResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        OAuth2AuthorizationRequest req = baseResolver.resolve(request, clientRegistrationId);
        if (req == null) return null;
        return customize(request, req);
    }

    private OAuth2AuthorizationRequest customize(HttpServletRequest request,
                                                 OAuth2AuthorizationRequest req) {
        String uri = request.getRequestURI();
        String registrationId = uri.substring(uri.lastIndexOf('/') + 1); // google|kakao|naver

        // 기본 파라미터 복사
        Map<String, Object> params = new LinkedHashMap<>(req.getAdditionalParameters());

        // ✅ 항상 동의창 유도 (DB 확인 가능하게 확장 가능)
        switch (registrationId) {
            case "google" -> params.put("prompt", "consent");
            case "kakao"  -> params.put("prompt", "login"); // or consent
            case "naver"  -> params.put("auth_type", "reprompt");
        }

        return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(params)
                .build();
    }
}
