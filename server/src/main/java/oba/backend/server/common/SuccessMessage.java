package oba.backend.server.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessMessage {
    SIGNUP_SUCCESS("회원가입이 성공적으로 완료되었습니다."),
    LOGIN_SUCCESS("로그인에 성공했습니다."),
    LOGOUT_SUCCESS("성공적으로 로그아웃되었습니다."),
    WITHDRAW_SUCCESS("성공적으로 회원 탈퇴되었습니다."),
    REISSUE_SUCCESS("토큰 재발급에 성공했습니다.");
    private final String message;
}