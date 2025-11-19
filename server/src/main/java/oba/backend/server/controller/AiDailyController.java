package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oba.backend.server.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiDailyController {

    private final AiService aiService;

    @PostMapping("/generate/daily")
    public ResponseEntity<String> generateDaily() {
        log.info("[AI] Daily GPT trigger received");
        return ResponseEntity.ok(aiService.runDailyAiJob());
    }
}
