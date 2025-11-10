package oba.backend.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.article.Article;
import oba.backend.server.domain.quiz.Quiz;
import oba.backend.server.domain.quiz.QuizRepository;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // ✅ Spring이 관리하는 Service 컴포넌트 (비즈니스 로직 담당)
@RequiredArgsConstructor // ✅ 생성자 주입 (final 필드 자동 주입)
public class QuizService {

    private final AiService aiService;           // 🧩 FastAPI와 통신하는 서비스 (뉴스 분석/퀴즈 생성 요청)
    private final QuizRepository quizRepository; // 🧩 MySQL의 quiz 테이블과 연결되는 JPA 리포지토리
    private final ObjectMapper mapper = new ObjectMapper(); // ✅ JSON 파싱용 Jackson 객체

    /**
     * ✅ 기사 URL을 기반으로 FastAPI 호출 → GPT 결과 저장
     * - 흐름 요약:
     *   1️⃣ MySQL에 있는 Article 객체에서 URL을 가져옴
     *   2️⃣ FastAPI에 URL을 전달 → GPT가 뉴스 분석 & 퀴즈 생성
     *   3️⃣ 응답(JSON)을 파싱해서 Quiz 엔티티로 변환
     *   4️⃣ MySQL의 quiz 테이블에 저장
     */
    @Transactional
    public void generateQuizFromArticle(Article article) {

        // ✅ FastAPI 요청에 필요한 DTO 생성
        // - AiRequestDto(url) : FastAPI에서 URL을 기반으로 뉴스 기사 크롤링 가능
        AiRequestDto request = AiRequestDto.builder()
                .url(article.getSource())  // ⚙️ Article의 뉴스 출처(URL)를 전달
                .build();

        // ✅ FastAPI 호출 (POST /analyze)
        // - FastAPI가 GPT 모델을 사용해 기사 분석 결과(AiResponseDto)를 반환
        AiResponseDto response = aiService.analyzeArticle(request);

        try {
            // ✅ GPT 응답을 JSON 형태로 변환 (String → JsonNode)
            JsonNode node = mapper.readTree(mapper.writeValueAsString(response));

            // ✅ 응답 내에 quizzes 배열이 존재할 경우 → 반복 저장
            if (node.has("quizzes")) {
                for (JsonNode quizNode : node.get("quizzes")) {

                    // 🧩 GPT 응답에서 각 퀴즈 정보를 꺼내 Quiz 엔티티 생성
                    Quiz quiz = Quiz.builder()
                            .articleId(article.getArticleId())                   // 어떤 기사에서 나온 퀴즈인지 연결
                            .question(quizNode.get("question").asText())          // 문제 내용
                            .options(quizNode.get("options").toString())          // 보기(JSON 배열) → 문자열로 변환
                            .correctAnswer(quizNode.get("answer").asText())       // 정답
                            .explanation(quizNode.get("explanation").asText())    // 해설
                            .summary(node.path("summary").asText())               // 기사 요약
                            .keywords(node.path("keywords").toString())           // 키워드 리스트(JSON 배열) → 문자열
                            .build();

                    // ✅ Quiz 엔티티를 MySQL DB에 저장
                    quizRepository.save(quiz);
                }
            } else {
                // GPT 응답에 "quizzes" 필드가 없을 때 로그 출력
                System.err.println("⚠️ GPT 응답에 퀴즈 데이터가 없습니다: " + response);
            }

        } catch (Exception e) {
            // JSON 파싱 또는 DB 저장 중 예외 발생 시 처리
            throw new RuntimeException("❌ 퀴즈 파싱 실패: " + e.getMessage(), e);
        }
    }
}
