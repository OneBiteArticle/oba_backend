package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ✅ CustomOAuth2UserService
 * Spring Security의 기본 OAuth2 로그인 서비스(DefaultOAuth2UserService)를 확장하여
 * Google, Kakao, Naver 등 외부 OAuth2 Provider로부터 받은 사용자 정보를
 * 우리 서비스의 User 엔티티와 매핑하고 DB에 저장하는 역할을 수행.
 *
 * OAuth2 로그인 흐름:
 * 1️⃣ 사용자가 OAuth2 Provider에서 로그인
 * 2️⃣ Access Token을 이용해 사용자 정보(UserInfo) API 호출
 * 3️⃣ 이 클래스가 실행되어 사용자 정보를 파싱하고 User 엔티티로 변환 및 저장
 * 4️⃣ Spring Security 인증 객체(UserPrincipal)로 반환
 */
@Service  // ✅ Spring Bean으로 등록되어 SecurityConfig에서 자동 주입 가능
@RequiredArgsConstructor  // ✅ UserRepository를 생성자 주입
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // ✅ 사용자 정보를 DB에 저장하거나 조회하기 위한 Repository
    private final UserRepository userRepository;

    /**
     * OAuth2 로그인 성공 후, 사용자 정보를 로드하여 UserPrincipal로 변환
     */
    @SuppressWarnings("unchecked") // ✅ Kakao/Naver의 attributes는 중첩 Map 구조이므로 캐스팅 경고 억제
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // ✅ 1. 기본적으로 OAuth2 Provider에서 사용자 정보를 불러옴 (access token 사용)
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // ✅ 2. 현재 로그인 중인 OAuth2 Provider의 ID (google, kakao, naver 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // ✅ 3. Provider에서 전달된 원본 사용자 정보(JSON 형태)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // ✅ Provider별로 다른 구조의 사용자 정보를 담기 위한 공통 변수
        String providerUserId;  // 공급자에서 제공한 고유 사용자 ID
        String email = null, name = null, picture = null;

        /**
         * ✅ 4. Provider별 사용자 정보 파싱
         * Provider마다 응답 JSON 구조가 다르기 때문에 각각 따로 처리
         */
        switch (registrationId) {
            case "google" -> {
                // Google 응답 예시:
                // { sub=10365716562491128609, email=jihun@example.com, name=변지훈, picture=https://... }
                providerUserId = (String) attributes.get("sub");
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                picture = (String) attributes.get("picture");
            }
            case "kakao" -> {
                // Kakao 응답 예시:
                // { id=4453787353, kakao_account={ email=..., profile={nickname=..., profile_image_url=...} } }
                providerUserId = String.valueOf(attributes.get("id"));
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");

                    // ✅ profile 내부에서 닉네임, 프로필 이미지 추출
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null) {
                        name = (String) profile.get("nickname");
                        picture = (String) profile.get("profile_image_url");
                    }
                }
            }
            case "naver" -> {
                // Naver 응답 예시:
                // { response={ id=abc123, email=jihun@example.com, name=변지훈, profile_image=https://... } }
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                providerUserId = (String) response.get("id");
                email = (String) response.get("email");
                name = (String) response.get("name");
                picture = (String) response.get("profile_image");
            }
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인 타입: " + registrationId);
        }

        // ✅ 5. 고유 식별자(identifier) 생성 — Provider + ProviderUserId 조합
        // 예: google:10365716562491128609 / kakao:4453787353
        String identifier = registrationId + ":" + providerUserId;

        /**
         * ✅ 6. DB에서 사용자 조회 or 신규 생성
         * - 이미 등록된 사용자인지 확인
         * - 없으면 새로 생성 (최초 로그인)
         */
        User user = userRepository.findByIdentifier(identifier)
                .orElse(User.builder()
                        .identifier(identifier)
                        .provider(ProviderInfo.valueOf(registrationId.toUpperCase())) // GOOGLE, KAKAO, NAVER
                        .role(Role.USER)  // 기본 권한 부여
                        .build());

        // ✅ 7. 사용자 정보 갱신 (이메일, 이름, 프로필 이미지)
        user.updateInfo(email, name, picture);

        // ✅ 8. 변경사항 저장 (최초 로그인 시 insert, 이후 로그인 시 update)
        userRepository.save(user);

        /**
         * ✅ 9. ROLE_USER 권한 부여 및 UserPrincipal 생성
         * - Spring Security 내부 인증 객체로 반환되어 SecurityContext에 저장됨
         */
        return new UserPrincipal(
                identifier,                     // 고유 사용자 식별자 (ex. google:1036..., kakao:4453...)
                email,                           // 사용자 이메일
                attributes,                      // Provider 원본 속성
                List.of(new SimpleGrantedAuthority("ROLE_USER"))  // 기본 권한 (ROLE_USER)
        );
    }
}
