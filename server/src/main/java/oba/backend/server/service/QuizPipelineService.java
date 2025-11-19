package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.entity.ArticleEntity;
import oba.backend.server.repository.ArticleRepository;
import oba.backend.server.domain.mongo.NewsResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ✅ QuizPipelineService
 * MySQL + MongoDB 연동하여 퀴즈 자동 생성 파이프라인 담당
 */
@Service
@RequiredArgsConstructor
public class QuizPipelineService {

    private final ArticleRepository articleRepository;
    private final NewsResultRepository newsResultRepository;
    private final QuizService quizService;

    /**
     * ✅ MySQL Article 테이블 기반으로 FastAPI 호출 → 퀴즈 자동 생성
     */
    public void generateQuizzesFromMongo() {
        List<ArticleEntity> articles = articleRepository.findAll();

        if (articles.isEmpty()) {
            System.out.println("⚠️ MySQL에 등록된 기사 없음 (MongoDB ObjectId 연결 불가)");
            return;
        }

        for (ArticleEntity article : articles) {
            try {
                System.out.println("🧩 퀴즈 생성 시작: articleId=" + article.getArticleId() + ", mongoId=" + article.getMongoId());
                quizService.generateQuizFromArticle(article);
                System.out.println("✅ 퀴즈 생성 완료: " + article.getArticleId());
            } catch (Exception e) {
                System.err.println("❌ 퀴즈 생성 실패 (articleId=" + article.getArticleId() + "): " + e.getMessage());
            }
        }
    }
}
