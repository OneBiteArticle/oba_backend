package oba.backend.server.service;

import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.security.oauth.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    private UserRepository userRepository;
    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        customOAuth2UserService = new CustomOAuth2UserService(userRepository);
    }

    @Test
    void 새로운_사용자가_DB에_저장된다() {
        // given
        Map<String, Object> attributes = Map.of(
                "sub", "1234567890",
                "email", "test@example.com",
                "name", "테스트 유저",
                "picture", "http://test.com/profile.png"
        );

        // 최소 ROLE_USER 권한 부여 (IllegalArgumentException 방지)
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Set.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub"
        );
        assertThat(oAuth2User.getAttributes().get("email")).isEqualTo("test@example.com");

        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        var clientRegistration = TestOAuth2Utils.createClientRegistration("google");
        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);

        // super.loadUser() Mocking
        CustomOAuth2UserService service = new CustomOAuth2UserService(userRepository) {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest ignored) {
                String registrationId = "google";

                String id = (String) attributes.get("sub");
                String email = (String) attributes.get("email");
                String name = (String) attributes.get("name");
                String picture = (String) attributes.get("picture");

                User user = userRepository.findByIdentifier(id)
                        .orElse(User.builder()
                                .identifier(id)
                                .provider(ProviderInfo.valueOf(registrationId.toUpperCase()))
                                .role(Role.USER)
                                .build());

                user.updateInfo(email, name, picture);
                userRepository.save(user);

                return oAuth2User;
            }
        };

        when(userRepository.findByIdentifier("1234567890")).thenReturn(Optional.empty());

        // when
        service.loadUser(userRequest);

        // then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User savedUser = captor.getValue();

        assertThat(savedUser.getIdentifier()).isEqualTo("1234567890");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(savedUser.getName()).isEqualTo("테스트 유저");
        assertThat(savedUser.getProvider()).isEqualTo(ProviderInfo.GOOGLE);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }
}
