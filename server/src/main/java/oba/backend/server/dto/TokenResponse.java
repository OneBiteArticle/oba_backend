package oba.backend.server.dto;

public record TokenResponse(
        String grantType,
        String accessToken,
        String refreshToken
) {
}