package oba.backend.server.domain.mongo;

import oba.backend.server.domain.mongo.NewsResultDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * ✅ NewsResultRepository
 * - MongoDB의 news_results 컬렉션과 연동되는 Repository 인터페이스
 * - Spring Data MongoDB가 자동으로 구현체를 만들어 주기 때문에
 *   개발자가 직접 쿼리를 작성하지 않아도 됨
 * - NewsResultDocument(문서 엔티티)를 MongoDB에 저장/조회/삭제하는 기능 제공
 */
public interface NewsResultRepository extends MongoRepository<NewsResultDocument, String> {
    // ⬆️ MongoRepository<문서클래스, 기본키타입>
    //    첫 번째 타입: MongoDB 컬렉션에 매핑되는 Document 클래스
    //    두 번째 타입: @Id 필드 타입 (여기서는 String, ObjectId 대응)

    /**
     * ✅ articleId로 특정 뉴스 분석 결과 찾기
     * - MongoDB 필드명: articleId
     * - SQL로 치면: SELECT * FROM news_results WHERE articleId = ?
     *
     * @param articleId 기사 ID (MySQL Article과 동일한 ID)
     * @return Optional<NewsResultDocument> 해당 문서(없을 수도 있음)
     */
    Optional<NewsResultDocument> findByArticleId(Long articleId);

    /**
     * ✅ 특정 사용자의 오답이 포함된 뉴스 결과 목록 조회
     * - MongoDB의 중첩 필드(wrongAnswers.userId)를 탐색함
     * - SQL로 치면: SELECT * FROM news_results WHERE wrongAnswers.userId = ?
     *
     * @param userId 사용자 ID (MySQL User와 동일)
     * @return List<NewsResultDocument> 해당 사용자의 오답이 포함된 뉴스 결과 리스트
     */
    List<NewsResultDocument> findByWrongAnswersUserId(Long userId);
}
