package oba.backend.server.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * ✅ QuizRepository
 * - MySQL의 Quiz 테이블에 접근하기 위한 Spring Data JPA 리포지토리
 * - Quiz 엔티티(@Entity)와 연결되어 있으며,
 *   CRUD(Create, Read, Update, Delete) 기능을 자동으로 제공함
 */
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    /**
     * ✅ 기사(articleId)에 해당하는 퀴즈 목록 조회
     * - FastAPI가 하나의 뉴스(articleId)를 분석해 여러 개의 퀴즈를 생성할 수 있으므로
     *   한 기사당 여러 퀴즈(Quiz 엔티티)가 존재할 수 있음
     *
     * 예시 SQL:
     *  SELECT * FROM quiz WHERE article_id = ?;
     *
     * @param articleId : 기사 고유 ID (MySQL의 Article.articleId와 동일)
     * @return 해당 기사에 연결된 Quiz 리스트
     */
    List<Quiz> findByArticleId(Long articleId);
}
