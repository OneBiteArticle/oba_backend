package oba.backend.server.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @GetMapping("/profile")
    public Object profile(@AuthenticationPrincipal OAuth2User user) {
        // GitHub에서 가져온 전체 프로필 JSON 반환
        // 예: { id: 123, login: "...", email: "...", ... }
        return user.getAttributes();
    }
}
