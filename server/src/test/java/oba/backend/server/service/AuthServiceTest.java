package oba.backend.server.service;

import oba.backend.server.dto.AuthDto.LoginRequest;
import oba.backend.server.dto.AuthDto.SignUpRequest;
import oba.backend.server.dto.AuthDto.TokenResponse;
import oba.backend.server.entity.Member;
import oba.backend.server.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        // 테스트용 회원가입 DTO를 email, nickname 기반으로 수정
        signUpRequest = new SignUpRequest("test@example.com", "testuser", "Password123!");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("회원가입 성공: 올바른 정보로 가입 시 DB에 정상적으로 저장된다.")
    void signUp_success() {
        // when
        authService.signUp(signUpRequest);

        // then
        // email로 사용자를 조회
        Member foundMember = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com")
                .orElseThrow(() -> new AssertionError("테스트 실패: 사용자를 찾을 수 없습니다."));

        // email과 nickname이 일치하는지 확인
        assertThat(foundMember.getEmail()).isEqualTo("test@example.com");
        assertThat(foundMember.getNickname()).isEqualTo("testuser");
        assertTrue(passwordEncoder.matches("Password123!", foundMember.getPassword()));
        assertFalse(foundMember.isWithdrawn());
    }

    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 (활성) 이메일로 가입을 시도하면 예외가 발생한다.")
    void signUp_fail_duplicateEmail() {
        // given
        authService.signUp(signUpRequest); // 먼저 'test@example.com'으로 가입
        SignUpRequest duplicateEmailRequest = new SignUpRequest("test@example.com", "newuser", "Password123!");

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(duplicateEmailRequest);
        });
        assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 이메일입니다.");
    }

    @Test
    @DisplayName("회원가입 실패: 탈퇴한 회원의 이메일로 가입을 시도하면 예외가 발생한다.")
    void signUp_fail_emailOfWithdrawnUser() {
        // given
        // 1. 사용자를 가입시킨다.
        authService.signUp(signUpRequest);
        // 2. 해당 사용자를 탈퇴 처리한다 (Soft Delete).
        Member member = memberRepository.findByEmail("test@example.com").get();
        member.withdraw();
        memberRepository.save(member);

        // 3. 탈퇴한 회원의 이메일과 동일한 이메일로 가입을 시도한다. (닉네임은 다르게)
        SignUpRequest withdrawnEmailRequest = new SignUpRequest("test@example.com", "anotheruser", "Password123!");

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(withdrawnEmailRequest);
        });
        // 상세한 예외 메시지가 발생하는지 확인한다.
        assertThat(exception.getMessage()).isEqualTo("탈퇴한 이력이 있는 이메일입니다. 다른 이메일을 사용해주세요.");
    }


    @Test
    @DisplayName("회원가입 실패: 이미 존재하는 닉네임으로 가입을 시도하면 예외가 발생한다.")
    void signUp_fail_duplicateNickname() {
        // given
        authService.signUp(signUpRequest); // 먼저 'testuser' 닉네임으로 가입
        SignUpRequest duplicateNicknameRequest = new SignUpRequest("new@example.com", "testuser", "Password123!");

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.signUp(duplicateNicknameRequest);
        });
        assertThat(exception.getMessage()).isEqualTo("이미 사용 중인 닉네임입니다.");
    }


    @Test
    @DisplayName("로그인 성공: 올바른 이메일과 비밀번호로 로그인하면 토큰이 발급된다.")
    void login_success() {
        // given
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");

        // when
        TokenResponse tokenResponse = authService.login(loginRequest);

        // then
        assertThat(tokenResponse).isNotNull();
        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();

        Member member = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").get();
        assertThat(member.getRefreshToken()).isEqualTo(tokenResponse.refreshToken());
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 비밀번호로 로그인하면 예외가 발생한다.")
    void login_fail_wrongPassword() {
        // given
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "WrongPassword123!");

        // when & then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("로그인 실패: 탈퇴한 회원으로 로그인하면 예외가 발생한다.")
    void login_fail_withdrawnUser() {
        // given
        authService.signUp(signUpRequest);
        Member member = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").get();
        member.withdraw();
        memberRepository.save(member);

        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");

        // when & then
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    @DisplayName("토큰 재발급 성공: 유효한 Refresh Token으로 재발급을 요청하면 새로운 토큰들이 발급된다.")
    void reissue_success() throws InterruptedException {
        // given
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        TokenResponse initialTokenResponse = authService.login(loginRequest);
        String initialRefreshToken = initialTokenResponse.refreshToken();

        Thread.sleep(1000);

        // when
        TokenResponse reissuedTokenResponse = authService.reissue(initialRefreshToken);

        // then
        assertThat(reissuedTokenResponse).isNotNull();
        assertThat(reissuedTokenResponse.accessToken()).isNotEqualTo(initialTokenResponse.accessToken());
        assertThat(reissuedTokenResponse.refreshToken()).isNotEqualTo(initialTokenResponse.refreshToken());

        Member member = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").get();
        assertThat(member.getRefreshToken()).isEqualTo(reissuedTokenResponse.refreshToken());
    }

    @Test
    @DisplayName("회원 탈퇴 성공 (Soft Delete): 탈퇴 요청 시 isWithdrawn이 true로 변경된다.")
    void deleteMember_success_softDelete() {
        // given
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        authService.login(loginRequest);

        // SecurityContext에 email을 principal로 하는 인증 정보 설정
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "test@example.com", null, java.util.Collections.singletonList(() -> "ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        authService.deleteMember();

        // then
        assertFalse(memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").isPresent());

        Member withdrawnMember = memberRepository.findAll().stream()
                .filter(m -> m.getEmail().equals("test@example.com"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("테스트 실패: 탈퇴한 사용자를 DB에서 찾을 수 없습니다."));

        assertTrue(withdrawnMember.isWithdrawn());
        assertThat(withdrawnMember.getRefreshToken()).isNull();
    }

    @Test
    @DisplayName("로그아웃 성공: 로그아웃 요청 시 DB의 Refresh Token이 null로 변경된다.")
    void logout_success() {
        // given
        authService.signUp(signUpRequest);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "Password123!");
        authService.login(loginRequest);

        Member memberBeforeLogout = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").get();
        assertThat(memberBeforeLogout.getRefreshToken()).isNotNull();

        // SecurityContext에 email을 principal로 하는 인증 정보 설정
        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "test@example.com", null, java.util.Collections.singletonList(() -> "ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        authService.logout();

        // then
        Member memberAfterLogout = memberRepository.findByEmailAndIsWithdrawnFalse("test@example.com").get();
        assertThat(memberAfterLogout.getRefreshToken()).isNull();
    }
}
