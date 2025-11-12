package oba.backend.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.NewsResultRepository;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.dto.quiz.SubmitRequestDto;
import oba.backend.server.entity.ArticleEntity;
import oba.backend.server.entity.QuizEntity;
import oba.backend.server.repository.ArticleRepository;
import oba.backend.server.repository.QuizRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ✅ QuizService
 * MongoDB ObjectId 기반으로 FastAPI를 호출해 퀴즈 생성
 */
@Service
@RequiredArgsConstructor
public class QuizService {

    private final AiPipelineService aiPipelineService; // ✅ 교체됨
    private final QuizRepository quizRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final ArticleRepository articleRepository;
    private final NewsResultRepository newsResultRepository;

    /**
     * ✅ MongoDB ObjectId 기반으로 FastAPI 호출 → GPT 응답을 퀴즈로 저장
     */
    @Transactional
    public void generateQuizFromArticle(ArticleEntity article) {

        // 🧩 MongoDB ObjectId 추출
        String mongoId = article.getMongoId();
        if (mongoId == null) {
            throw new IllegalArgumentException("⚠️ 해당 Article에 mongoId가 없습니다: " + article.getArticleId());
        }

        // ✅ FastAPI 호출
        AiResponseDto response = aiPipelineService.analyzeArticle(new ObjectId(mongoId));

        try {
            JsonNode node = mapper.readTree(mapper.writeValueAsString(response));

            if (node.has("quizzes")) {
                for (JsonNode quizNode : node.get("quizzes")) {
                    QuizEntity quiz = QuizEntity.builder()
                            .articleId(article.getArticleId())
                            .question(quizNode.get("question").asText())
                            .options(quizNode.get("options").toString())
                            .correctAnswer(quizNode.get("answer").asText())
                            .explanation(quizNode.get("explanation").asText())
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
    public List<QuizEntity> getQuizByArticle(Long articleId) {
        return quizRepository.findByArticleId(articleId);
    }

    // ✅ 퀴즈 제출 결과 처리 (단순 응답)
    public String submitQuizResults(SubmitRequestDto request) {
        return "퀴즈 결과가 정상적으로 저장되었습니다.";
    }
}
