package oba.backend.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.SelectedArticleDocument;
import oba.backend.server.repository.SelectedArticlesRepository;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ✅ AiPipelineService (MongoDB 중심)
 */
@Service
@RequiredArgsConstructor
public class AiPipelineService {

    private final SelectedArticlesRepository selectedArticlesRepository;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    private static final String FASTAPI_URL = "http://oba_ai_service:8000/analyze";

    public String processTodayArticles() {
        String today = LocalDate.now().toString();
        List<SelectedArticleDocument> articles = selectedArticlesRepository.findByServingDate(today);

        for (SelectedArticleDocument doc : articles) {
            processSingleArticle(doc.getUrl(), doc.getArticleId());
        }

        return "✅ 오늘(" + today + ") 기사 분석 완료 (" + articles.size() + "건)";
    }

    public AiResponseDto processSingleArticle(String url, Long articleId) {
        AiResponseDto aiResponse = callFastApi(url, articleId);
        saveAIResultToMongo(articleId, aiResponse);
        return aiResponse;
    }

    private void saveAIResultToMongo(Long articleId, AiResponseDto aiResult) {
        SelectedArticleDocument doc = selectedArticlesRepository.findByArticleId(articleId);
        if (doc == null) return;

        doc.setAiSummary(aiResult.getSummary());
        doc.setAiKeywords(aiResult.getKeywords());

        // ✅ QuizItem → Map<String, Object> 변환
        doc.setAiQuizzes(
                aiResult.getQuizzes()
                        .stream()
                        .map(q -> objectMapper.convertValue(q, new TypeReference<Map<String, Object>>() {}))
                        .toList()
        );

        selectedArticlesRepository.save(doc);
    }

    private AiResponseDto callFastApi(String url, Long articleId) {
        return webClientBuilder.build()
                .post()
                .uri(FASTAPI_URL)
                .bodyValue(new AiRequestDto(articleId, url))
                .retrieve()
                .bodyToMono(AiResponseDto.class)
                .block();
    }
}
