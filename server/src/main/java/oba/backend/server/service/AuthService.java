package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.LoginRequest;
import oba.backend.server.dto.SignUpRequest;
import oba.backend.server.dto.TokenResponse;
import oba.backend.server.entity.Member;
import oba.backend.server.entity.Role;
import oba.backend.server.jwt.JwtProvider;
import oba.backend.server.repository.MemberRepository;
import oba.backend.server.security.SecurityUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final SecurityUtil securityUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.email())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.password());

        Member member = Member.builder()
                .email(signUpRequest.email())
                .nickname(signUpRequest.nickname())
                .password(encodedPassword)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember() {
        Member member = securityUtil.getCurrentMember();
        memberRepository.delete(member);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        Member member = memberRepository.findByEmailOrThrow(authentication.getName());
        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token 입니다.");
        }

        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다. 다시 로그인해주세요."));

        Authentication authentication = jwtProvider.getAuthentication(member.getEmail());
        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

    @Transactional
    public void logout() {
        Member member = securityUtil.getCurrentMember();
        member.updateRefreshToken(null);
    }
}
