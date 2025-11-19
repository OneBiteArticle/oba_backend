package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    // application.yml → ai.server.url
    @Value("${ai.server.url}")
    private String fastapiUrl;

    public String runDailyAiJob() {
        log.info("[AI] FastAPI 호출 시작 → {}", fastapiUrl);

        try {
            ResponseEntity<String> response =
                    restTemplate.postForEntity(fastapiUrl, null, String.class);

            log.info("[AI] FastAPI 응답 수신: status={}, bodyLength={}",
                    response.getStatusCode(),
                    response.getBody() != null ? response.getBody().length() : 0);

            return response.getBody();

        } catch (Exception e) {
            log.error("[AI] FastAPI 호출 실패", e);
            throw new RuntimeException("FastAPI 호출 실패", e);
        }
    }
}
