package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.service.AiPipelineService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiPipelineService aiPipelineService;

    // ✅ 오늘 날짜의 기사 AI 처리 요청
    @PostMapping("/analyze/today")
    public String analyzeTodayArticles() {
        return aiPipelineService.processTodayArticles();
    }

    // ✅ 개별 기사 URL 분석 요청
    @PostMapping("/analyze")
    public AiResponseDto analyzeSingle(@RequestParam String url, @RequestParam Long articleId) {
        return aiPipelineService.processSingleArticle(url, articleId);
    }
}
