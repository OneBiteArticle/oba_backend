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

        // 뒤로가기 캐시 방지(선택)
        resp.setHeader("Cache-Control", "no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        boolean loggedIn = (user != null);
        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            Map<String, Object> attrs = user.getAttributes();
            String name = null;
            Object n = attrs.get("name");                     // google
            if (n == null) {
                Object kakaoAcc = attrs.get("kakao_account"); // kakao
                if (kakaoAcc instanceof Map<?,?> kakao) {
                    Object profile = kakao.get("profile");
                    if (profile instanceof Map<?,?> p) n = p.get("nickname");
                }
            }
            if (n == null) {
                Object naverResp = attrs.get("response");     // naver
                if (naverResp instanceof Map<?,?> respMap) n = respMap.get("name");
            }
            model.addAttribute("userName", n != null ? n.toString() : user.getName());
        }
        return "login";
    }
}
