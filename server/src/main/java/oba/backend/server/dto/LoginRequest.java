package oba.backend.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 로그인 요청 DTO (username -> email)
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}