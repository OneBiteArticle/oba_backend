package oba.backend.server.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ✅ CustomAuthorizationRequestResolver
 * OAuth2 인증 요청 시, 요청 파라미터를 커스터마이징하기 위한 클래스.
 * 기본적으로 Spring Security의 DefaultOAuth2AuthorizationRequestResolver를 확장하여
 * 로그인 시 Provider별(google, kakao, naver 등)로 요청 파라미터를 조정할 수 있음.
 */
@RequiredArgsConstructor  // 생성자 주입 자동 생성 (clientRegistrationRepository, userRepository 주입)
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    // ✅ OAuth2 Provider(Google, Kakao 등)의 client 설정 정보를 저장하는 Spring Bean
    private final ClientRegistrationRepository clientRegistrationRepository;

    // ✅ 사용자 DB에 접근하기 위한 Repository (필요 시 사용자 상태 확인 등에 활용 가능)
    private final UserRepository userRepository;

    // ✅ OAuth2 기본 요청 경로 (Spring Security 내부 기본값: "/oauth2/authorization")
    private final String authorizationRequestBaseUri = "/oauth2/authorization";

    /**
     * ✅ (1) 사용자가 /oauth2/authorization/{provider} 로 접근했을 때 호출됨
     * - OAuth2 로그인 요청을 감지하여, 기본 Resolver로 요청 객체를 만든 뒤 커스터마이징 수행
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        // 기본 OAuth2 요청 생성기 (Spring이 제공)
        DefaultOAuth2AuthorizationRequestResolver baseResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);

        // 기본 요청 객체 생성 (provider 정보 포함)
        OAuth2AuthorizationRequest req = baseResolver.resolve(request);
        if (req == null) return null; // 요청이 없으면 null 반환

        // 요청을 커스터마이징하여 반환
        return customize(request, req);
    }

    /**
     * ✅ (2) 특정 provider ID(google, kakao, naver 등)를 지정해 요청할 때 호출됨
     * - 위 메서드와 거의 동일하지만 명시적으로 clientRegistrationId를 전달받음
     */
    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        DefaultOAuth2AuthorizationRequestResolver baseResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, authorizationRequestBaseUri);
        OAuth2AuthorizationRequest req = baseResolver.resolve(request, clientRegistrationId);
        if (req == null) return null;

        // 요청 파라미터 수정 및 반환
        return customize(request, req);
    }

    /**
     * ✅ (3) 실제 요청 파라미터를 커스터마이징하는 핵심 로직
     * - 각 Provider별로 ‘항상 동의창 표시’, ‘다시 로그인 유도’ 등의 옵션을 강제로 설정
     * - 예: Google → prompt=consent, Kakao → prompt=login, Naver → auth_type=reprompt
     */
    private OAuth2AuthorizationRequest customize(HttpServletRequest request,
                                                 OAuth2AuthorizationRequest req) {

        // 요청 URI에서 provider 이름 추출 (ex. /oauth2/authorization/google → google)
        String uri = request.getRequestURI();
        String registrationId = uri.substring(uri.lastIndexOf('/') + 1);

        // 기본 요청 파라미터를 복사 (기존 값 유지 + 수정)
        Map<String, Object> params = new LinkedHashMap<>(req.getAdditionalParameters());

        // ✅ 각 Provider별 추가 파라미터 지정
        // - Google: 항상 동의창(prompt=consent)
        // - Kakao: 항상 로그인창(prompt=login)
        // - Naver: 항상 재동의창(auth_type=reprompt)
        switch (registrationId) {
            case "google" -> params.put("prompt", "consent");
            case "kakao"  -> params.put("prompt", "login");
            case "naver"  -> params.put("auth_type", "reprompt");
        }

        // ✅ 수정된 파라미터를 포함한 새로운 요청 객체를 생성해 반환
        return OAuth2AuthorizationRequest.from(req)  // 기존 요청 복사
                .additionalParameters(params)        // 수정된 파라미터 추가
                .build();                            // 새 AuthorizationRequest 생성
    }
}
