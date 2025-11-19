package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 🔥 권한 반환 (ROLE_USER, ROLE_ADMIN 방식)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getName() {
        return user.getName();
    }

    // 🔥 JWT 발급 시 식별자 반환
    public String getIdentifier() {
        return user.getIdentifier();
    }

    public User getUser() {
        return user;
    }
}
