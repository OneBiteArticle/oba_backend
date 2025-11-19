package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User, UserDetails {

    private final User user;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 기존 username = identifier
    @Override
    public String getName() {
        return user.getIdentifier();
    }

    // 👍 USER role 꺼낼 수 있게 추가
    public String getRole() {
        return user.getRole().name(); // USER / ADMIN
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRole().getAuthorities();
    }

    @Override
    public String getPassword() { return null; }

    @Override
    public String getUsername() { return user.getIdentifier(); }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
