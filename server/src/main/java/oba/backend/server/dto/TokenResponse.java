package oba.backend.server.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
