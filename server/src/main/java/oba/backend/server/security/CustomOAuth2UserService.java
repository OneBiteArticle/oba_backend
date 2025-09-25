package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google|kakao|naver
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerUserId;
        String email = null, name = null, picture = null;

        switch (registrationId) {
            case "google" -> {
                providerUserId = (String) attributes.get("sub");
                email = (String) attributes.get("email");
                name = (String) attributes.get("name");
                picture = (String) attributes.get("picture");
            }
            case "kakao" -> {
                providerUserId = String.valueOf(attributes.get("id"));

                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email"); // 동의 안 하면 null
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null) {
                        name = (String) profile.get("nickname");
                        picture = (String) profile.get("profile_image_url");
                    }
                }
            }
            case "naver" -> {
                Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                providerUserId = (String) response.get("id");
                email = (String) response.get("email");
                name = (String) response.get("name");
                picture = (String) response.get("profile_image");
            }
            default -> throw new OAuth2AuthenticationException("지원하지 않는 로그인 타입: " + registrationId);
        }

        String identifier = registrationId + ":" + providerUserId;

        User user = userRepository.findByIdentifier(identifier)
                .orElse(User.builder()
                        .identifier(identifier)
                        .provider(ProviderInfo.valueOf(registrationId.toUpperCase()))
                        .role(Role.USER)
                        .build());

        user.updateInfo(email, name, picture);
        userRepository.save(user);

        return new UserPrincipal(identifier, email, attributes, registrationId);
    }
}
