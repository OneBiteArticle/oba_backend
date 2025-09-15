package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    // Spring Security의 인터페이스 메소드 이름은 loadUserByUsername이지만,
    // 실제로는 로그인 시 입력된 email이 이 파라미터로 들어오게 됩니다.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // --- [변경 1] findByUsername -> findByEmail ---
        return memberRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getRole().getKey());

        // --- [변경 2] getUsername -> getEmail ---
        // Spring Security의 User 객체를 생성할 때, username으로 email을 사용합니다.
        // 이것이 인증의 주체가 됩니다.
        return new User(
                member.getEmail(),
                member.getPassword(),
                Collections.singletonList(grantedAuthority)
        );
    }
}
