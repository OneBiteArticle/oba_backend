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

    // 수동 실행 API (Postman/관리자 테스트용)
    @PostMapping("/generate/daily")
    public ResponseEntity<String> runDailyAi() {

        System.out.println("▶ /ai/generate/daily 요청 들어옴");

        // AiService의 실제 메서드 호출
        String result = aiService.runDailyGptTask();

        return ResponseEntity.ok(result);
    }
}
