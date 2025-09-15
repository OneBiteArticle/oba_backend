package oba.backend.server.repository;

import oba.backend.server.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // --- [변경 1] findByUsername -> findByEmail ---
    // Spring Data JPA가 메소드 이름을 분석하여 email 필드로 검색하는 쿼리를 자동으로 생성합니다.
    Optional<Member> findByEmail(String email);

    // --- [변경 2] existsByUsername -> existsByEmail ---
    boolean existsByEmail(String email);

    // findByRefreshToken은 변경할 필요가 없습니다.
    Optional<Member> findByRefreshToken(String refreshToken);

    /**
     * --- [변경 3] findByUsernameOrThrow -> findByEmailOrThrow ---
     * email로 Member를 찾아 반환하고, 없으면 예외를 발생시키는 default 메소드
     * @param email 찾고자 하는 사용자의 email
     * @return 찾아낸 Member 엔티티
     */
    default Member findByEmailOrThrow(String email) {
        // 내부적으로 새로 정의한 findByEmail을 호출합니다.
        return findByEmail(email).orElseThrow(() ->
                new RuntimeException("해당 사용자를 찾을 수 없습니다: " + email)
        );
    }
}
