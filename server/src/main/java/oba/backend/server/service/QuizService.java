package oba.backend.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.NewsResultRepository;

import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.dto.quiz.SubmitRequestDto;
import oba.backend.server.entity.ArticleEntity;
import oba.backend.server.entity.QuizEntity;           // ✅ 수정됨
import oba.backend.server.repository.ArticleRepository;
import oba.backend.server.repository.QuizRepository;   // ✅ 수정됨

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final AiService aiService;
    private final QuizRepository quizRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ArticleRepository articleRepository;
    private final NewsResultRepository newsResultRepository;

    /**
     * ✅ MySQL의 ArticleEntity 기반으로 FastAPI 호출 → GPT 응답을 퀴즈로 저장
     */
    @Transactional
    public void generateQuizFromArticle(ArticleEntity article) {

        // ✅ FastAPI 요청 DTO 구성
        AiRequestDto request = AiRequestDto.builder()
                .url(article.getSource()) // ⚙️ ArticleEntity의 URL or Source
                .build();

        // ✅ FastAPI 호출
        AiResponseDto response = aiService.analyzeArticle(request);

        try {
            JsonNode node = mapper.readTree(mapper.writeValueAsString(response));

            if (node.has("quizzes")) {
                for (JsonNode quizNode : node.get("quizzes")) {
                    QuizEntity quiz = QuizEntity.builder()
                            .articleId(article.getArticleId())
                            .question(quizNode.get("question").asText())
                            .options(quizNode.get("options").toString())
                            .correctAnswer(quizNode.get("answer").asText())
                            .build();

                    quizRepository.save(quiz);
                }
            } else {
                System.err.println("⚠️ GPT 응답에 퀴즈 데이터가 없습니다: " + response);
            }

        } catch (Exception e) {
            throw new RuntimeException("❌ 퀴즈 파싱 실패: " + e.getMessage(), e);
        }
    }

    // ✅ 기사별 퀴즈 조회
    public List<?> getQuizByArticle(Long articleId) {
        return quizRepository.findByArticleId(articleId);
    }

    // ✅ 퀴즈 제출 결과 처리
    public String submitQuizResults(SubmitRequestDto request) {
        return "퀴즈 결과가 정상적으로 저장되었습니다.";
    }
}
