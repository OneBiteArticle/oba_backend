package oba.backend.server.service;

import oba.backend.server.dto.LoginRequest;
import oba.backend.server.dto.SignUpRequest;
import oba.backend.server.dto.TokenResponse;
import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private SignUpRequest signUpRequest;

    @BeforeEach
    void setUp() {
        // email / password / nickname
        signUpRequest = new SignUpRequest("testuser@naver.com", "tester", "Password123!");
    }

    @Test
    @DisplayName("회원가입 성공: 올바른 정보로 가입하면 DB에 저장된다")
    void signUp_success() {
        // when
        authService.signUp(signUpRequest);

        // then
        Member foundMember = memberRepository.findByEmail("testuser@naver.com")
                .orElseThrow(() -> new AssertionError("사용자를 찾을 수 없습니다."));

        assertThat(foundMember.getEmail()).isEqualTo("testuser@naver.com");
        assertThat(foundMember.getNickname()).isEqualTo("tester");
        assertTrue(passwordEncoder.matches("Password123!", foundMember.getPassword()));
    }

    @Test
    @DisplayName("회원가입 실패: 중복된 이메일 가입 시 예외 발생")
    void signUp_fail_duplicateEmail() {
        authService.signUp(signUpRequest);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(signUpRequest);
        });

        assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공: 올바른 이메일/비밀번호 입력 시 토큰 발급")
    void login_success() {
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("testuser@naver.com", "Password123!");

        TokenResponse tokenResponse = authService.login(loginRequest);

        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();

        Member member = memberRepository.findByEmail("testuser@naver.com").get();
        assertThat(member.getRefreshToken()).isEqualTo(tokenResponse.refreshToken());
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호 입력 시 예외 발생")
    void login_fail_wrongPassword() {
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("testuser@naver.com", "WrongPassword123!");

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("로그아웃 성공: 로그아웃 시 DB의 Refresh Token이 null로 변경된다")
    void logout_success() {
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("testuser@naver.com", "Password123!");
        authService.login(loginRequest);

        Member memberBeforeLogout = memberRepository.findByEmail("testuser@naver.com").get();
        assertThat(memberBeforeLogout.getRefreshToken()).isNotNull();

        var authentication = new UsernamePasswordAuthenticationToken(
                "testuser@naver.com", null, Collections.singletonList(() -> "ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        authService.logout();

        Member memberAfterLogout = memberRepository.findByEmail("testuser@naver.com").get();
        assertThat(memberAfterLogout.getRefreshToken()).isNull();
    }

}
