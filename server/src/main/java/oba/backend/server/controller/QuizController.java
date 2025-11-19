package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final MongoTemplate mongoTemplate;

    // ✅ 기사별 퀴즈 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<?> getQuizByArticle(@PathVariable("articleId") Long articleId) {
        Query query = new Query(Criteria.where("article_id").is(articleId));
        Document doc = mongoTemplate.findOne(query, Document.class, "Selected_Articles");

        if (doc == null) {
            return ResponseEntity.status(404).body(Map.of("error", "해당 기사에 대한 퀴즈 데이터 없음"));
        }

        List<Map<String, Object>> quizzes = (List<Map<String, Object>>) doc.get("quizzes");
        if (quizzes == null || quizzes.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "AI 퀴즈가 없습니다."));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("articleId", articleId);
        response.put("title", doc.getString("title"));
        response.put("quizzes", quizzes);

        return ResponseEntity.ok(response);
    }
}
