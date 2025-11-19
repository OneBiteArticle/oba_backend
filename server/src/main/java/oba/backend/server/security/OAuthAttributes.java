package oba.backend.server.security;

import java.util.Map;

public record OAuthAttributes(
        String id,
        String email,
        String name,
        String picture,
        Map<String, Object> attributes
) {

    public static OAuthAttributes of(String provider, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            case "naver" -> ofNaver(attributes);
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }

    private static OAuthAttributes ofGoogle(Map<String, Object> attr) {
        return new OAuthAttributes(
                (String) attr.get("sub"),
                (String) attr.get("email"),
                (String) attr.get("name"),
                (String) attr.get("picture"),
                attr
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(Map<String, Object> attr) {
        Map<String, Object> account = (Map<String, Object>) attr.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return new OAuthAttributes(
                String.valueOf(attr.get("id")),
                (String) account.get("email"),
                (String) profile.get("nickname"),
                (String) profile.get("profile_image_url"),
                attr
        );
    }

    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofNaver(Map<String, Object> attr) {
        Map<String, Object> response = (Map<String, Object>) attr.get("response");

        return new OAuthAttributes(
                (String) response.get("id"),
                (String) response.get("email"),
                (String) response.get("name"),
                (String) response.get("profile_image"),
                attr
        );
    }
}
