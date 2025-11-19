package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.security.OAuthAttributes;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoVerifier {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthAttributes verify(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = response.getBody();
        Map account = (Map) body.get("kakao_account");
        Map profile = (Map) account.get("profile");

        return new OAuthAttributes(
                String.valueOf(body.get("id")),
                (String) account.get("email"),
                (String) profile.get("nickname"),
                (String) profile.get("profile_image_url"),
                body
        );
    }
}
