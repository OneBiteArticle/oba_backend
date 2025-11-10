package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.NewsResultDocument;
import oba.backend.server.domain.mongo.NewsResultRepository;
import oba.backend.server.domain.mongo.UserWrongAnswer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ✅ NewsResultService
 * - FastAPI가 생성한 뉴스 분석 결과(GPT 응답)를 MongoDB에 저장하고
 *   기사별 / 사용자별 데이터 조회 및 오답 기록을 관리하는 비즈니스 로직 계층
 * - Controller ↔ Service ↔ Repository 구조 중 ‘Service’ 레이어
 */
@Service // 스프링이 관리하는 서비스 빈으로 등록
@RequiredArgsConstructor // final 필드에 대해 생성자 자동 주입 (DI)
public class NewsResultService {

    // ✅ MongoDB 접근용 Repository (Spring Data MongoRepository 상속)
    private final NewsResultRepository newsResultRepository;

    // ----------------------------------------------------------------------
    // 🧩 1️⃣ FastAPI가 생성한 결과 저장
    // ----------------------------------------------------------------------
    /**
     * FastAPI가 반환한 뉴스 분석 결과(AiResponseDto 형태)를 MongoDB에 저장하는 메서드
     *
     * @param document : MongoDB에 저장할 문서(뉴스 분석 결과)
     * @return 저장된 NewsResultDocument (저장 완료된 결과)
     *
     * 동작 순서:
     * 1️⃣ FastAPI → Spring 서버로 결과 JSON 전송
     * 2️⃣ Spring이 이를 NewsResultDocument로 매핑
     * 3️⃣ saveResult()가 MongoDB에 저장 (Repository 이용)
     */
    @Transactional // 데이터베이스 트랜잭션 보장 (쓰기 작업 시 안전성 확보)
    public NewsResultDocument saveResult(NewsResultDocument document) {
        return newsResultRepository.save(document);
    }

    // ----------------------------------------------------------------------
    // 🧩 2️⃣ 특정 기사(articleId) 결과 조회
    // ----------------------------------------------------------------------
    /**
     * articleId(기사 ID)를 기준으로 MongoDB에 저장된 분석 결과를 조회
     *
     * @param articleId : 조회할 기사 고유 ID
     * @return 해당 기사의 NewsResultDocument
     *
     * 동작 예시:
     *  GET /api/mongo/101 → findByArticleId(101)
     *
     * MongoDB 쿼리 예시:
     *  db.news_results.findOne({ "articleId": 101 })
     */
    public NewsResultDocument getResultByArticleId(Long articleId) {
        return newsResultRepository.findByArticleId(articleId)
                .orElseThrow(() -> new RuntimeException("해당 기사의 결과를 찾을 수 없습니다."));
    }

    // ----------------------------------------------------------------------
    // 🧩 3️⃣ 사용자 오답 추가 (틀린 문제 기록)
    // ----------------------------------------------------------------------
    /**
     * 사용자가 퀴즈를 풀고 틀린 경우, 해당 오답 정보를 MongoDB 문서에 추가하는 메서드
     *
     * @param articleId : 오답이 속한 기사 ID
     * @param wrongAnswer : UserWrongAnswer 객체 (userId, 문제번호, 선택지 등 포함)
     *
     * 동작 과정:
     *  1️⃣ articleId로 MongoDB 문서 조회
     *  2️⃣ 해당 문서의 wrongAnswers 리스트에 새로운 오답 추가
     *  3️⃣ 변경된 문서를 다시 MongoDB에 저장
     *
     * 예시:
     *  db.news_results.updateOne(
     *    { articleId: 101 },
     *    { $push: { wrongAnswers: { userId: 1234, questionId: "Q1", selected: "보기2" } } }
     *  )
     */
    @Transactional
    public void addWrongAnswer(Long articleId, UserWrongAnswer wrongAnswer) {
        // 1️⃣ 기사 ID로 해당 문서 조회 (없으면 예외 발생)
        NewsResultDocument document = newsResultRepository.findByArticleId(articleId)
                .orElseThrow(() -> new RuntimeException("결과 문서를 찾을 수 없습니다."));

        // 2️⃣ 기존 오답 리스트에 새로운 오답 추가
        document.getWrongAnswers().add(wrongAnswer);

        // 3️⃣ 변경된 문서를 다시 MongoDB에 저장
        newsResultRepository.save(document);
    }

    // ----------------------------------------------------------------------
    // 🧩 4️⃣ 특정 사용자(userId)의 오답 전체 조회
    // ----------------------------------------------------------------------
    /**
     * 특정 사용자가 풀었던 모든 기사 중 오답이 포함된 결과를 조회
     *
     * @param userId : 사용자 ID
     * @return 해당 사용자가 틀린 문제를 포함한 NewsResultDocument 목록
     *
     * 예시:
     *  GET /api/mongo/user/1234/wrong
     *
     * MongoDB 쿼리 예시:
     *  db.news_results.find({ "wrongAnswers.userId": 1234 })
     */
    public List<NewsResultDocument> getUserWrongAnswers(Long userId) {
        return newsResultRepository.findByWrongAnswersUserId(userId);
    }
}
