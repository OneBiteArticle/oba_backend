package oba.backend.server.security;

import lombok.RequiredArgsConstructor;
import oba.backend.server.security.OAuthAttributes;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NaverVerifier {

    private final RestTemplate restTemplate = new RestTemplate();

    public OAuthAttributes verify(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map body = (Map) response.getBody().get("response");

        return new OAuthAttributes(
                (String) body.get("id"),
                (String) body.get("email"),
                (String) body.get("name"),
                (String) body.get("profile_image"),
                body
        );
    }
}
