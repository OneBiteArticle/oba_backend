package oba.backend.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.SelectedArticleDocument;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.repository.SelectedArticlesRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AiPipelineService {

    private final WebClient webClient;
    private final SelectedArticlesRepository selectedArticlesRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${external.fastapi.url}")
    private String fastApiUrl; // e.g., http://127.0.0.1:8000/analyze

    public AiResponseDto analyzeArticle(ObjectId objectId) {
        // 1️⃣ MongoDB에서 기사 조회
        SelectedArticleDocument article = selectedArticlesRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + objectId));

        // 2️⃣ FastAPI로 ObjectId 전달
        return webClient.post()
                .uri(fastApiUrl)
                .bodyValue("{\"object_id\": \"" + objectId + "\"}")
                .retrieve()
                .bodyToMono(AiResponseDto.class)
                .block();
    }
}
