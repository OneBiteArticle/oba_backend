package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.service.AiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/analyze")
    public AiResponseDto analyze(@RequestBody AiRequestDto request) {
        return aiService.analyzeArticle(request);
    }

    @PostMapping("/generate_news_content")
    public AiResponseDto generateContent(@RequestBody AiRequestDto request) {
        return aiService.generateNewsContent(request);
    }
}
