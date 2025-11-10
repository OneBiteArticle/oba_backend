package oba.backend.server.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ✅ UserPrincipal
 * - Spring Security에서 인증된 사용자 정보를 나타내는 클래스
 * - UserDetails(일반 로그인) + OAuth2User(소셜 로그인) 을 모두 구현함
 * - 따라서, 어떤 로그인 방식이든 일관된 형태로 사용자 정보를 관리할 수 있음
 */
@Getter
public class UserPrincipal implements OAuth2User, UserDetails {

    // ✅ 고유 식별자 (identifier)
    // 예: "google:1234567890" 또는 "kakao:987654321"
    private final String id;

    // ✅ 사용자 이메일
    // 일반적으로 OAuth2 provider가 제공 (Google, Naver 등)
    private final String email;

    // ✅ OAuth2 provider에서 전달된 전체 사용자 정보 (속성 맵)
    // 예: {name=홍길동, email=gildong@gmail.com, picture=http://...}
    private final Map<String, Object> attributes;

    // ✅ 사용자 권한 목록 (ROLE_USER, ROLE_ADMIN 등)
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * 생성자
     * - OAuth2 인증 정보를 기반으로 UserPrincipal 객체를 만듦
     * - attributes나 authorities가 null이면 비어있는 컬렉션으로 초기화
     */
    public UserPrincipal(String id,
                         String email,
                         Map<String, Object> attributes,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        // ✅ null 방지 처리 (빈 Map/List로 초기화)
        this.attributes = (attributes == null) ? Map.of() : Map.copyOf(attributes);
        this.authorities = (authorities == null) ? List.of() : List.copyOf(authorities);
    }

    // ----------------------------------------------------------------------
    // 🧩 OAuth2User 인터페이스 구현
    // ----------------------------------------------------------------------

    /**
     * ✅ getAttributes()
     * - OAuth2 provider에서 전달된 사용자 정보를 반환
     * - 예: 구글 로그인 시, {sub=..., name=..., email=...} 형태의 Map
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * ✅ getName()
     * - Spring Security 내부적으로 사용자 식별자(ID)로 사용됨
     * - 여기서는 identifier(예: google:123456789)를 반환
     */
    @Override
    public String getName() {
        return id;
    }

    // ----------------------------------------------------------------------
    // 🧩 UserDetails 인터페이스 구현 (Spring Security 기본 인증 구조)
    // ----------------------------------------------------------------------

    /**
     * ✅ getAuthorities()
     * - 사용자의 권한(Role) 목록을 반환
     * - 예: [ROLE_USER]
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * ✅ getPassword()
     * - 일반 로그인 시 비밀번호 반환하지만, OAuth2 로그인은 필요 없음 → null 반환
     */
    @Override
    public String getPassword() {
        return null; // 소셜 로그인 사용 시 패스워드 불필요
    }

    /**
     * ✅ getUsername()
     * - Spring Security에서 사용자를 식별할 때 사용
     * - 일반적으로 이메일을 username으로 사용
     */
    @Override
    public String getUsername() {
        return email;
    }

    // ----------------------------------------------------------------------
    // 🧩 계정 상태 관련 메서드 (true = 활성 상태)
    // ----------------------------------------------------------------------

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 아님
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 사용 가능
    }
}
