package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AuthDto.LoginRequest;
import oba.backend.server.dto.AuthDto.SignUpRequest;
import oba.backend.server.dto.AuthDto.TokenResponse;
import oba.backend.server.entity.Member;
import oba.backend.server.jwt.JwtProvider;
import oba.backend.server.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // AuthenticationManagerBuilder 대신 AuthenticationManager를 직접 주입
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        if (memberRepository.existsByUsername(signUpRequest.username())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signUpRequest.password());
        Member member = Member.builder()
                .username(signUpRequest.username())
                .password(encodedPassword)
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public void deleteMember() {
        // 1. SecurityContext에서 현재 인증된 사용자의 username을 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("인증 정보가 없는 요청입니다.");
        }
        String username = authentication.getName();

        // 2. DB에서 해당 사용자를 찾아옴
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 3. 사용자를 DB에서 삭제
        memberRepository.delete(member);
    }

    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());

        // 이제 authenticationManager를 직접 사용하여 인증
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        Member member = memberRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
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

        Authentication authentication = jwtProvider.getAuthentication(member.getUsername());
        TokenResponse tokenResponse = jwtProvider.generateToken(authentication);

        member.updateRefreshToken(tokenResponse.refreshToken());

        return tokenResponse;
    }

    @Transactional
    public void logout() {
        // 1. SecurityContext에서 현재 인증된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            // 이 경우는 보통 JwtAuthenticationFilter에서 토큰이 없을 때 걸러지지만, 안전을 위해 추가
            throw new RuntimeException("인증 정보가 없는 요청입니다.");
        }
        String username = authentication.getName();

        // 2. DB에서 해당 사용자를 찾아 Refresh Token을 null로 설정하여 무효화
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        member.updateRefreshToken(null);
    }
}