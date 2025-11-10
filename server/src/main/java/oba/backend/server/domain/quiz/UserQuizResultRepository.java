package oba.backend.server.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ✅ UserQuizResultRepository
 * - MySQL에 저장된 사용자 퀴즈 풀이 결과(UserQuizResult 엔티티)에 접근하기 위한 JPA Repository
 * - Spring Data JPA가 자동으로 기본 CRUD 메서드(save, findById, findAll 등)를 구현해줌
 * - 별도의 @Repository 어노테이션이 없어도 스프링이 자동으로 관리함
 */
public interface UserQuizResultRepository extends JpaRepository<UserQuizResult, Long> {

    // 💡 JpaRepository 기본 제공 메서드 예시:
    // save(UserQuizResult result) → 퀴즈 풀이 결과 저장
    // findById(Long resultId) → 특정 결과 조회
    // findAll() → 모든 사용자 퀴즈 결과 조회
    // deleteById(Long resultId) → 결과 삭제
    //
    // 추가적으로 필요하다면, 커스텀 쿼리 메서드도 정의 가능함
    // 예: List<UserQuizResult> findByUserId(Long userId);
}
