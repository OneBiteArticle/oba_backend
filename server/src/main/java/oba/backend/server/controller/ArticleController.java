package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import oba.backend.server.entity.ArticleEntity;
import oba.backend.server.repository.ArticleRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final MongoTemplate mongoTemplate;
    private final ArticleRepository articleRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable("id") Long id) {
        // ✅ 1️⃣ MySQL 기사 확인
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MySQL에 해당 기사가 없습니다."));

        // ✅ 2️⃣ MongoDB 조회 (article_id를 문자열로 변환)
        Query query = new Query(Criteria.where("article_id").is(String.valueOf(id)));
        Document doc = mongoTemplate.findOne(query, Document.class, "news_results");

        if (doc == null) {
            return ResponseEntity.status(404).body(Map.of("error", "MongoDB에 해당 기사 없음"));
        }

        // ✅ 3️⃣ 통합 응답 생성
        Map<String, Object> result = new HashMap<>();
        result.put("article_id", id);
        result.put("title", article.getTitle());
        result.put("category", article.getCategory());
        result.put("source", article.getSource());
        result.put("createdAt", article.getCreatedAt());
        result.put("url", doc.getString("url"));
        result.put("summary", doc.getString("summary"));
        result.put("keywords", doc.get("keywords"));
        result.put("content", doc.getString("content"));

        return ResponseEntity.ok(result);
    }
}
