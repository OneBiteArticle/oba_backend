package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        OAuth2User oAuth2User = super.loadUser(request);

        String provider = request.getClientRegistration().getRegistrationId(); // google, kakao, naver

        OAuthAttributes attributes = OAuthAttributes.of(provider, oAuth2User.getAttributes());

        // identifier = "google:고유ID"
        String identifier = provider + ":" + attributes.id();

        // DB 조회
        User user = userRepository.findByIdentifier(identifier)
                .map(existing -> {
                    existing.updateInfo(
                            attributes.email(),
                            attributes.name(),
                            attributes.picture()
                    );
                    return existing;
                })
                .orElseGet(() -> User.builder()
                        .identifier(identifier)
                        .email(attributes.email())
                        .name(attributes.name())
                        .picture(attributes.picture())
                        .provider(ProviderInfo.from(provider))
                        .role(Role.USER)
                        .build()
                );

        // 저장
        userRepository.save(user);

        // OAuth2User 반환
        return new CustomOAuth2User(user, attributes.attributes());
    }
}
