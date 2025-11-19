package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // 수동 실행 API (Postman 테스트용, 관리자용)
    @PostMapping("/generate/daily")
    public ResponseEntity<String> runDailyAi() {
        System.out.println("[Spring] /ai/generate/daily 요청 들어옴");
        String result = aiService.runDailyAiJob();
        return ResponseEntity.ok(result);
    }
}
