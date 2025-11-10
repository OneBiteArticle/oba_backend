package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.quiz.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * ✅ QuizController
 * - 사용자의 퀴즈 풀이 및 정답 제출을 처리하는 REST API 컨트롤러
 * - 주요 기능:
 *   1️⃣ 사용자가 제출한 답안을 DB의 정답과 비교
 *   2️⃣ 정답 여부에 따라 결과 메시지를 반환
 *   3️⃣ 사용자의 퀴즈 결과를 UserQuizResult 테이블에 저장
 */
@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성 (의존성 주입용)
public class QuizController {

    // ✅ 퀴즈 정답 정보를 조회하기 위한 Repository
    private final QuizRepository quizRepository;

    // ✅ 사용자의 퀴즈 제출 결과를 저장하기 위한 Repository
    private final UserQuizResultRepository resultRepository;

    // ----------------------------------------------------------------------
    // 🧩 1️⃣ 사용자 퀴즈 제출 API
    // ----------------------------------------------------------------------
    /**
     * ✅ 사용자가 퀴즈를 풀고 답안을 제출할 때 호출되는 엔드포인트
     *
     * 요청 방식: POST
     * 요청 URL: /api/quiz/{quizId}/submit
     *
     * 예시 요청:
     *   POST /api/quiz/10/submit?userAnswer=B&userId=1
     *
     * @param quizId     : 제출한 퀴즈의 고유 ID
     * @param userAnswer : 사용자가 선택한 보기 또는 답변
     * @param userId     : 현재 로그인한 사용자 ID (MySQL의 User.id)
     *
     * 동작 순서:
     *   1️⃣ 퀴즈 ID로 DB에서 정답 조회 (QuizRepository)
     *   2️⃣ 사용자의 답(userAnswer)과 정답(correctAnswer)을 비교
     *   3️⃣ 정답 여부(isCorrect)를 UserQuizResult에 저장
     *   4️⃣ 결과 메시지 + 해설(explanation)을 JSON으로 응답
     */
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long quizId,      // URL 경로에서 퀴즈 ID 추출
            @RequestParam String userAnswer, // 쿼리 파라미터로 전달된 사용자 답
            @RequestParam Long userId) {     // 쿼리 파라미터로 전달된 사용자 ID

        // ✅ 1. 퀴즈 존재 여부 확인
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        // ✅ 2. 정답 비교 (문자열 비교)
        boolean isCorrect = quiz.getCorrectAnswer().equals(userAnswer);

        // ✅ 3. 사용자 제출 결과를 UserQuizResult 테이블에 저장
        resultRepository.save(UserQuizResult.builder()
                .quizId(quizId)
                .userId(userId)
                .userAnswer(userAnswer)
                .isCorrect(isCorrect)
                .build());

        // ✅ 4. 클라이언트에게 결과 반환 (JSON 형태)
        // 반환 예시:
        // {
        //   "quizId": 10,
        //   "result": "✅ 정답입니다!",
        //   "explanation": "AI는 인공지능의 약자입니다."
        // }
        return ResponseEntity.ok(Map.of(
                "quizId", quizId,
                "result", isCorrect ? "✅ 정답입니다!" : "❌ 틀렸습니다.",
                "explanation", quiz.getExplanation()
        ));
    }
}
