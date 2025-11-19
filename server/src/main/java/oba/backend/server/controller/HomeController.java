package oba.backend.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal OAuth2User user,
                        HttpServletResponse resp,
                        Model model) {
        resp.setHeader("Cache-Control", "no-store, must-revalidate"); // 캐시 저장 금지
        resp.setHeader("Pragma", "no-cache");                         // HTTP/1.0 캐시 무효화
        resp.setDateHeader("Expires", 0);                             // 만료 시간 0으로 설정 (즉시 만료)

        boolean loggedIn = (user != null);
        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            Map<String, Object> attrs = user.getAttributes();

            String name = null;
            Object n = attrs.get("name");

            if (n == null) {
                Object kakaoAcc = attrs.get("kakao_account");
                if (kakaoAcc instanceof Map<?, ?> kakao) {
                    Object profile = kakao.get("profile");
                    if (profile instanceof Map<?, ?> p) n = p.get("nickname");
                }
            }

            if (n == null) {
                Object naverResp = attrs.get("response");
                if (naverResp instanceof Map<?, ?> respMap)
                    n = respMap.get("name");
            }

            model.addAttribute("userName", n != null ? n.toString() : user.getName());
        }

        return "login";
    }
}
