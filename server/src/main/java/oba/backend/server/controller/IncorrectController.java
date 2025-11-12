package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.incorrect.IncorrectArticle;
import oba.backend.server.domain.incorrect.IncorrectQuiz;
import oba.backend.server.service.IncorrectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incorrect")
@RequiredArgsConstructor
public class IncorrectController {

    private final IncorrectService incorrectService;

    @GetMapping("/{userId}/articles")
    public List<IncorrectArticle> getUserIncorrectArticles(@PathVariable Long userId) {
        return incorrectService.getUserIncorrectArticles(userId);
    }

    @GetMapping("/{userId}/quizzes")
    public List<IncorrectQuiz> getUserIncorrectQuizzes(@PathVariable Long userId) {
        return incorrectService.getUserIncorrectQuizzes(userId);
    }

    @PostMapping("/article")
    public Map<String, Object> saveIncorrectArticle(@RequestBody IncorrectArticle article) {
        incorrectService.saveIncorrectArticle(article);
        return Map.of("result", "success", "type", "article");
    }

    @PostMapping("/quiz")
    public Map<String, Object> saveIncorrectQuiz(@RequestBody IncorrectQuiz quiz) {
        incorrectService.saveIncorrectQuiz(quiz);
        return Map.of("result", "success", "type", "quiz");
    }
}
