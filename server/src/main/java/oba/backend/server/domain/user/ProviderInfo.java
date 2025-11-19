package oba.backend.server.domain.user;

public enum ProviderInfo {
    GOOGLE,
    KAKAO,
    NAVER;

    public static ProviderInfo from(String provider) {
        return ProviderInfo.valueOf(provider.toUpperCase());
    }
}
