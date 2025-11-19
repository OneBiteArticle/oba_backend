package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.mongo.NewsResultDocument;
import oba.backend.server.domain.mongo.UserWrongAnswer;
import oba.backend.server.service.NewsResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mongo")
@RequiredArgsConstructor
public class NewsResultController {

    private final NewsResultService newsResultService;
    @PostMapping("/save")
    public NewsResultDocument saveResult(@RequestBody NewsResultDocument document) {
        return newsResultService.saveResult(document);
    }

    @GetMapping("/{articleId}")
    public NewsResultDocument getResult(@PathVariable Long articleId) {
        return newsResultService.getResultByArticleId(articleId);
    }

    @PostMapping("/{articleId}/wrong")
    public Map<String, Object> addWrongAnswer(
            @PathVariable Long articleId,
            @RequestBody UserWrongAnswer wrongAnswer) {

        newsResultService.addWrongAnswer(articleId, wrongAnswer);

        return Map.of("result", "오답 저장 완료", "articleId", articleId);
    }

    @GetMapping("/user/{userId}/wrong")
    public List<NewsResultDocument> getUserWrongAnswers(@PathVariable Long userId) {
        return newsResultService.getUserWrongAnswers(userId);
    }
}
