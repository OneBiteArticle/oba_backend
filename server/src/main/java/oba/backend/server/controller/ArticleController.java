package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.ArticleSummaryResponse;
import oba.backend.server.service.ArticleSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleSummaryService articleSummaryService;

    // 홈 화면 최신 기사 조회
    @GetMapping("/latest")
    public ResponseEntity<List<ArticleSummaryResponse>> getLatest() {
        return ResponseEntity.ok(articleSummaryService.getLatestArticles(5));
    }
}
