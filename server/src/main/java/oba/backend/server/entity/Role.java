package oba.backend.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자"); // 추후 관리자 역할 추가를 위해 미리 설계

    private final String key;
    private final String title;

}