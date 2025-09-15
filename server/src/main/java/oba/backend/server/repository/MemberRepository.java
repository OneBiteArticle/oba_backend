package oba.backend.server.repository;

import oba.backend.server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Member> findByRefreshToken(String refreshToken);

    /**
     * --- [변경 3] findByUsernameOrThrow -> findByEmailOrThrow ---
     * email로 Member를 찾아 반환하고, 없으면 예외를 발생시키는 default 메소드
     *
     * @param email 찾고자 하는 사용자의 email
     * @return 찾아낸 Member 엔티티
     */
    default Member findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() ->
                new RuntimeException("해당 사용자를 찾을 수 없습니다: " + email)
        );
    }
}
