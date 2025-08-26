package oba.backend.server.repository;

import oba.backend.server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // email로 탈퇴하지 않은 사용자 조회 (로그인 등에서 사용)
    Optional<Member> findByEmailAndIsWithdrawnFalse(String email);

    // email로 모든 사용자 조회 (회원가입 시 중복 검사용)
    Optional<Member> findByEmail(String email);

    // nickname으로 모든 사용자 조회 (회원가입 시 중복 검사용)
    Optional<Member> findByNickname(String nickname);

    // refreshToken으로 탈퇴하지 않은 사용자 조회
    Optional<Member> findByRefreshTokenAndIsWithdrawnFalse(String refreshToken);
}
