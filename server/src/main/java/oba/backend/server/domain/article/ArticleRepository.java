package oba.backend.server.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ ArticleRepository
 * - Article 엔티티(MySQL의 article 테이블)에 접근하기 위한 JPA Repository 인터페이스
 * - Spring Data JPA가 자동으로 구현체를 만들어주기 때문에 별도의 구현 클래스가 필요 없음
 * - 기본적인 CRUD (Create, Read, Update, Delete) 기능을 상속받고,
 *   추가로 커스텀 쿼리(@Query)를 통해 특정 조건으로 데이터를 조회할 수 있음
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * ✅ findArticlesToProcess()
     * - 특정 시간 범위(`servingDate` 기준)에 포함되는 기사들을 조회하는 메서드
     * - 주로 “스케줄러(배치 작업)”에서 사용됨
     *
     * 예를 들어, 매일 오전 9시에 “지난 하루 동안의 기사들”을 불러와서
     * FastAPI에 전달 → AI 요약/퀴즈 생성 요청에 사용할 수 있음.
     *
     * JPQL 문법 사용:
     *   SELECT a FROM Article a WHERE a.servingDate BETWEEN :start AND :end
     *
     * 매개변수:
     *   @param start 시작 시각 (예: 오늘 00:00)
     *   @param end   종료 시각 (예: 오늘 23:59)
     *
     * 반환값:
     *   지정된 기간 내의 Article 리스트 (List<Article>)
     */
    @Query("SELECT a FROM Article a WHERE a.servingDate BETWEEN :start AND :end")
    List<Article> findArticlesToProcess(LocalDateTime start, LocalDateTime end);
}
