package oba.backend.server.domain.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.LinkedHashMap;
import java.util.Map;

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

        // 요청 파라미터 수정 및 반환
        return customize(request, req);
    }

    private OAuth2AuthorizationRequest customize(HttpServletRequest request,
                                                 OAuth2AuthorizationRequest req) {

        String uri = request.getRequestURI();
        String registrationId = uri.substring(uri.lastIndexOf('/') + 1);

        Map<String, Object> params = new LinkedHashMap<>(req.getAdditionalParameters());

        switch (registrationId) {
            case "google" -> params.put("prompt", "consent");
            case "kakao"  -> params.put("prompt", "login");
            case "naver"  -> params.put("auth_type", "reprompt");
        }

        return OAuth2AuthorizationRequest.from(req)
                .additionalParameters(params)
                .build();
    }
}
