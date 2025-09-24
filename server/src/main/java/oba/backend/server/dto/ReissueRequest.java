package oba.backend.server.dto;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(

        @NotBlank String refreshToken
) {
}