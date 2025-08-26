package oba.backend.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AuthDto {

    // 회원가입 요청 DTO
    public record SignUpRequest(
            @NotBlank(message = "이메일은 필수 입력 값입니다.")
            @Email(message = "유효한 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "닉네임은 필수 입력 값입니다.")
            @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
            String nickname,

            @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
            @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}",
                    message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8자 ~ 20자의 비밀번호여야 합니다.")
            String password
    ) {
    }

    // 로그인 요청 DTO (username -> email)
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    // 토큰 정보 DTO (변경 없음)
    public record TokenResponse(
            String grantType,
            String accessToken,
            String refreshToken
    ) {
    }

    // 토큰 갱신 요청 DTO (변경 없음)
    public record ReissueRequest(
            @NotBlank String refreshToken
    ) {
    }
}
