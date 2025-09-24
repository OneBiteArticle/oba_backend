package oba.backend.server.security;

import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final MemberRepository memberRepository;

    public SecurityUtil(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AuthenticationCredentialsNotFoundException("Security Context에 인증 정보가 없습니다.");
        }

        return authentication.getName();
    }

    public Member getCurrentMember() {
        String email = getCurrentUsername();
        return memberRepository.findByEmailOrThrow(email);
    }

}
