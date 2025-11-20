package oba.backend.server.security.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import oba.backend.server.domain.user.ProviderInfo;
import oba.backend.server.domain.user.User;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String name;
    private String email;
    private String picture;
    private String provider; // google / kakao / naver

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        switch (provider.toLowerCase()) {
            case "google":
                return ofGoogle(attributes);
            case "kakao":
                return ofKakao(attributes);
            case "naver":
                return ofNaver(attributes);
        }
        throw new RuntimeException("지원하지 않는 provider: " + provider);
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attr) {
        return OAuthAttributes.builder()
                .name((String) attr.get("name"))
                .email((String) attr.get("email"))
                .picture((String) attr.get("picture"))
                .provider("google")
                .attributes(attr)
                .build();
    }

    private static OAuthAttributes ofKakao(Map<String, Object> attr) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attr.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .provider("kakao")
                .attributes(attr)
                .build();
    }

    private static OAuthAttributes ofNaver(Map<String, Object> attr) {
        Map<String, Object> res = (Map<String, Object>) attr.get("response");

        return OAuthAttributes.builder()
                .name((String) res.get("name"))
                .email((String) res.get("email"))
                .picture((String) res.get("profile_image"))
                .provider("naver")
                .attributes(attr)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .identifier(provider + ":" + email)
                .email(email)
                .name(name)
                .picture(picture)
                .provider(ProviderInfo.valueOf(provider.toUpperCase()))
                .build();
    }
}
