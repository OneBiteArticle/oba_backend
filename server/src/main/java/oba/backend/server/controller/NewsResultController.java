package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.NewsResultDocument;
import oba.backend.server.domain.mongo.UserWrongAnswer;
import oba.backend.server.service.NewsResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ✅ NewsResultController
 * - FastAPI로부터 받은 뉴스 분석 결과(AI 생성 결과)를 MongoDB에 저장하고,
 *   사용자별/기사별 데이터를 조회 및 관리하는 REST API 컨트롤러
 * - 데이터 처리는 모두 NewsResultService에서 담당하고, 이 클래스는 요청/응답만 관리
 */
@RestController // JSON 형태의 REST API 응답을 반환하는 컨트롤러
@RequestMapping("/api/mongo") // 모든 엔드포인트의 기본 URL 경로
@RequiredArgsConstructor // final 필드 자동 주입 생성자 생성
public class NewsResultController {

    // ✅ 비즈니스 로직을 담당하는 서비스 계층 의존성 주입
    private final NewsResultService newsResultService;

    // ----------------------------------------------------------------------
    // 🧩 1️⃣ FastAPI 결과 MongoDB 저장
    // ----------------------------------------------------------------------
    /**
     * FastAPI가 생성한 뉴스 분석 결과(AiResponseDto → NewsResultDocument)를 MongoDB에 저장
     *
     * 예시 요청:
     * POST /api/mongo/save
     * Body(JSON):
     * {
     *   "articleId": 101,
     *   "url": "https://news.naver.com/article/001/0012345678",
     *   "summary": "뉴스 요약 내용",
     *   "keywords": ["AI", "산업", "기술"],
     *   "quizzes": [...],
     *   "result": "OK"
     * }
     *
     * 반환: 저장된 MongoDB Document(JSON)
     */
    @PostMapping("/save")
    public NewsResultDocument saveResult(@RequestBody NewsResultDocument document) {
        // 요청 본문(JSON)을 NewsResultDocument 객체로 매핑하여 서비스 계층으로 전달
        return newsResultService.saveResult(document);
    }

    // ----------------------------------------------------------------------
    // 🧩 2️⃣ 기사별 결과 조회
    // ----------------------------------------------------------------------
    /**
     * 특정 뉴스 기사(articleId)에 대한 분석 결과를 MongoDB에서 조회
     *
     * 예시 요청:
     * GET /api/mongo/101
     *
     * 반환(JSON):
     * {
     *   "articleId": 101,
     *   "summary": "...",
     *   "keywords": [...],
     *   "quizzes": [...]
     * }
     */
    @GetMapping("/{articleId}")
    public NewsResultDocument getResult(@PathVariable Long articleId) {
        // articleId를 경로에서 받아 MongoDB에서 해당 결과 조회
        return newsResultService.getResultByArticleId(articleId);
    }

    // ----------------------------------------------------------------------
    // 🧩 3️⃣ 사용자 오답 추가
    // ----------------------------------------------------------------------
    /**
     * 사용자가 퀴즈를 틀렸을 때, 해당 오답 정보를 MongoDB에 기록
     *
     * 예시 요청:
     * POST /api/mongo/101/wrong
     * Body(JSON):
     * {
     *   "userId": 1234,
     *   "questionId": "Q3",
     *   "selectedOption": "보기 2",
     *   "correctAnswer": "보기 4"
     * }
     *
     * 반환(JSON):
     * {
     *   "result": "오답 저장 완료",
     *   "articleId": 101
     * }
     */
    @PostMapping("/{articleId}/wrong")
    public Map<String, Object> addWrongAnswer(
            @PathVariable Long articleId,
            @RequestBody UserWrongAnswer wrongAnswer) {

        // 서비스 계층에서 MongoDB의 해당 기사 문서에 오답 추가
        newsResultService.addWrongAnswer(articleId, wrongAnswer);

        // 성공 메시지 반환
        return Map.of("result", "오답 저장 완료", "articleId", articleId);
    }

    // ----------------------------------------------------------------------
    // 🧩 4️⃣ 특정 사용자 오답 전체 조회
    // ----------------------------------------------------------------------
    /**
     * 특정 사용자(userId)가 푼 모든 뉴스 기사 중에서 오답이 포함된 결과를 조회
     *
     * 예시 요청:
     * GET /api/mongo/user/1234/wrong
     *
     * 반환(JSON 배열):
     * [
     *   {
     *     "articleId": 101,
     *     "title": "AI 기술 뉴스",
     *     "wrongAnswers": [...]
     *   },
     *   {
     *     "articleId": 205,
     *     "title": "로봇산업 전망",
     *     "wrongAnswers": [...]
     *   }
     * ]
     */
    @GetMapping("/user/{userId}/wrong")
    public List<NewsResultDocument> getUserWrongAnswers(@PathVariable Long userId) {
        // 특정 userId가 포함된 모든 오답 기록 조회
        return newsResultService.getUserWrongAnswers(userId);
    }
}
