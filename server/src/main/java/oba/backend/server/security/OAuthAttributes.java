package oba.backend.server.security;

import java.util.Map;

/**
 * 모바일 소셜 로그인(Google/Kakao/Naver) 공통 사용자 정보 DTO
 * - MobileAuthController에서 DB 저장 및 JWT 발급에 사용
 * - Verifier들이 반환하는 공통 구조
 */
public record OAuthAttributes(
        String id,
        String email,
        String name,
        String picture,
        Map<String, Object> attributes
) { }
