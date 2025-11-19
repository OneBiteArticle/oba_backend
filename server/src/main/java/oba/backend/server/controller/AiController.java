package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate/daily")
    public ResponseEntity<String> runDailyAi() {
        log.info("[AI] /ai/generate/daily 요청 수신");
        String result = aiService.runDailyAiJob();
        return ResponseEntity.ok(result);
    }
}

