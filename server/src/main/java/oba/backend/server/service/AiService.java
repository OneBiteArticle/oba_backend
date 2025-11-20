package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();

    // Docker Compose 내부 FastAPI 주소
    private final String FASTAPI_URL = "http://ai_backend:8000/generate_daily_gpt_results";

    public String runDailyGptTask() {
        System.out.println("[Spring] FastAPI 호출 시작 → " + FASTAPI_URL);

        ResponseEntity<String> response =
                restTemplate.postForEntity(FASTAPI_URL, null, String.class);

        System.out.println("[Spring] FastAPI 응답 수신:");
        System.out.println(response.getBody());

        return response.getBody();
    }
}
