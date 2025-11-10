package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.quiz.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizRepository quizRepository;

    private final UserQuizResultRepository resultRepository;

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long quizId,
            @RequestParam String userAnswer,
            @RequestParam Long userId) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("퀴즈를 찾을 수 없습니다."));

        boolean isCorrect = quiz.getCorrectAnswer().equals(userAnswer);

        resultRepository.save(UserQuizResult.builder()
                .quizId(quizId)
                .userId(userId)
                .userAnswer(userAnswer)
                .isCorrect(isCorrect)
                .build());

        return ResponseEntity.ok(Map.of(
                "quizId", quizId,
                "result", isCorrect ? "정답입니다!" : "틀렸습니다.",
                "explanation", quiz.getExplanation()
        ));
    }
}
