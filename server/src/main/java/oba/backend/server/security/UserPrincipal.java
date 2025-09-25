package oba.backend.server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 사용자 래퍼
 */
public class UserPrincipal implements OAuth2User, UserDetails {

    private final String id;
    private final String email;
    private final Map<String, Object> attributes;
    private final String provider;

    public UserPrincipal(String id, String email, Map<String, Object> attributes, String provider) {
        this.id = id;
        this.email = email;
        this.attributes = attributes;
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    // OAuth2User
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return id;
    }

    // UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email != null ? email : id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
