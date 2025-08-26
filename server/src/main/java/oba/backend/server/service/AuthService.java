package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AuthDto.LoginRequest;
import oba.backend.server.dto.AuthDto.SignUpRequest;
import oba.backend.server.dto.AuthDto.TokenResponse;
import oba.backend.server.entity.Member;
import oba.backend.server.jwt.JwtProvider;
import oba.backend.server.repository.MemberRepository;
import oba.backend.server.security.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        // 1. 이메일로 회원 조회 (탈퇴 여부 무관)
        Optional<Member> existingMemberByEmail = memberRepository.findByEmail(signUpRequest.email());
        if (existingMemberByEmail.isPresent()) {
            if (existingMemberByEmail.get().isWithdrawn()) {
                throw new RuntimeException("탈퇴한 이력이 있는 이메일입니다. 다른 이메일을 사용해주세요.");
            } else {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
        }

        // 2. 닉네임으로 회원 조회 (탈퇴 여부 무관)
        Optional<Member> existingMemberByNickname = memberRepository.findByNickname(signUpRequest.nickname());
        if (existingMemberByNickname.isPresent()) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 중복이 없으면 회원가입 진행
        String encodedPassword = passwordEncoder.encode(signUpRequest.password());
        Member member = Member.builder()
                .email(signUpRequest.email())
                .nickname(signUpRequest.nickname())
                .password(encodedPassword)
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("인증 정보가 없는 요청입니다.");
        }
        String email = authentication.getName();

        Member member = memberRepository.findByEmailAndIsWithdrawnFalse(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        member.withdraw();
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        Member member = memberRepository.findByEmailAndIsWithdrawnFalse(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token 입니다.");
        }

        Member member = memberRepository.findByRefreshTokenAndIsWithdrawnFalse(refreshToken)
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다. 다시 로그인해주세요."));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(member.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("인증 정보가 없는 요청입니다.");
        }
        String email = authentication.getName();

        Member member = memberRepository.findByEmailAndIsWithdrawnFalse(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        member.updateRefreshToken(null);
    }
}
