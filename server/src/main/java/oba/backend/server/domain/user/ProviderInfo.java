package oba.backend.server.domain.user;

public enum ProviderInfo {
    GOOGLE,
    KAKAO,
    NAVER;

    public static ProviderInfo from(String provider) {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("provider must not be null or blank");
        }

        try {
            return ProviderInfo.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported provider: " + provider, e);
        }
    }
}