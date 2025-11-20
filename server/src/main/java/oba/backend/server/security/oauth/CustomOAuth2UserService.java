package oba.backend.server.security.oauth;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import oba.backend.server.security.oauth.dto.OAuthAttributes;
import oba.backend.server.security.oauth.dto.CustomOAuth2User;
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

        String provider = request.getClientRegistration().getRegistrationId();
        OAuthAttributes attributes = OAuthAttributes.of(provider, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attr) {

        String identifier = attr.getProvider() + ":" + attr.getEmail();

        return userRepository.findByIdentifier(identifier)
                .map(user -> {
                    user.updateInfo(attr.getEmail(), attr.getName(), attr.getPicture());
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .identifier(identifier)
                            .email(attr.getEmail())
                            .name(attr.getName())
                            .picture(attr.getPicture())
                            .provider(ProviderInfo.valueOf(attr.getProvider().toUpperCase()))
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });
    }
}
