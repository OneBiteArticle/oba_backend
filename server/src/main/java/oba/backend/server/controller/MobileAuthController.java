package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.common.jwt.JwtProvider;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.Role;
import oba.backend.server.domain.user.User;
import oba.backend.server.domain.user.UserRepository;
import oba.backend.server.dto.TokenResponse;
import oba.backend.server.security.GoogleVerifier;
import oba.backend.server.security.KakaoVerifier;
import oba.backend.server.security.NaverVerifier;
import oba.backend.server.security.OAuthAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/mobile")
public class MobileAuthController {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    private final GoogleVerifier googleVerifier;
    private final KakaoVerifier kakaoVerifier;
    private final NaverVerifier naverVerifier;

    /**
     * 🔹 Google 모바일 로그인
     *    RN → idToken 전달
     */
    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleLogin(@RequestBody Map<String, String> body) {

        String idToken = body.get("idToken");
        var payload = googleVerifier.verify(idToken);

        return ResponseEntity.ok(
                processLogin(
                        "google",
                        payload.getSubject(),
                        payload.getEmail(),
                        (String) payload.get("name"),
                        (String) payload.get("picture")
                )
        );
    }

    /**
     * 🔹 Kakao 모바일 로그인
     *    RN → accessToken 전달
     */
    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody Map<String, String> body) {

        String accessToken = body.get("accessToken");
        OAuthAttributes kakao = kakaoVerifier.verify(accessToken);

        return ResponseEntity.ok(
                processLogin(
                        "kakao",
                        kakao.id(),
                        kakao.email(),
                        kakao.name(),
                        kakao.picture()
                )
        );
    }

    /**
     * 🔹 Naver 모바일 로그인
     *    RN → accessToken 전달
     */
    @PostMapping("/naver")
    public ResponseEntity<TokenResponse> naverLogin(@RequestBody Map<String, String> body) {

        String accessToken = body.get("accessToken");
        OAuthAttributes naver = naverVerifier.verify(accessToken);

        return ResponseEntity.ok(
                processLogin(
                        "naver",
                        naver.id(),
                        naver.email(),
                        naver.name(),
                        naver.picture()
                )
        );
    }

    /**
     * 🔥 공통 로그인 처리 메서드
     * - DB 조회 및 생성
     * - JWT 발급
     */
    private TokenResponse processLogin(
            String provider,
            String providerId,
            String email,
            String name,
            String picture
    ) {

        String identifier = provider + ":" + providerId;

        // DB 조회 또는 생성
        User user = userRepository.findByIdentifier(identifier)
                .map(u -> {
                    u.updateInfo(email, name, picture);
                    return u;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .identifier(identifier)
                                .email(email)
                                .name(name)
                                .picture(picture)
                                .provider(ProviderInfo.from(provider))
                                .role(Role.USER)
                                .build()
                ));

        // Spring Security Authentication 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getIdentifier(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // JWT 발급(JSON 반환)
        return jwtProvider.generateToken(auth);
    }
}
