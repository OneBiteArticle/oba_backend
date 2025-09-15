package oba.backend.server.security;

import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final MemberRepository memberRepository;

    public SecurityUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String getCurrentUsername() { // Spring Security 컨텍스트에서는 여전히 'username'으로 통칭됩니다.
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

    public Member getCurrentMember() {
        // --- [변경] findByUsername -> findByEmail ---
        // Security Context에서 가져온 사용자의 이름(이제 email)을 사용하여 DB에서 Member를 찾습니다.
        String email = getCurrentUsername();
        return memberRepository.findByEmailOrThrow(email);
    }
}
