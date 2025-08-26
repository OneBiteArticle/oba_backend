package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
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

    // Spring Security는 내부적으로 username이라는 파라미터명을 사용하므로 메소드명은 그대로 둡니다.
    // 하지만 실제로는 email을 사용하여 사용자를 조회합니다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmailAndIsWithdrawnFalse(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
    }

    // DB 에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴
    // User 객체의 첫 번째 인자인 username 필드에 email을 사용합니다.
    private UserDetails createUserDetails(Member member) {
        return new User(
                member.getEmail(), // username 대신 email 사용
                member.getPassword(),
                Collections.singletonList(() -> "ROLE_USER")
        );
    }
}
