package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import oba.backend.server.entity.ArticleEntity;
import oba.backend.server.repository.ArticleRepository;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final MongoTemplate mongoTemplate;
    private final ArticleRepository articleRepository;

    // ✅ 1️⃣ 기사 상세 조회 (MySQL + MongoDB 통합)
    @GetMapping("/{id}")
    public ResponseEntity<?> getArticle(@PathVariable("id") Long id) {
        // 🧩 MySQL 기사 메타데이터 조회
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MySQL에 해당 기사가 없습니다."));

        // 🧩 MongoDB 기사 본문 + 요약 조회
        Query query = new Query(Criteria.where("article_id").is(id));
        Document doc = mongoTemplate.findOne(query, Document.class, "Selected_Articles");

        if (doc == null) {
            return ResponseEntity.status(404).body(Map.of("error", "MongoDB에 해당 기사 없음"));
        }

        // 🧠 content_col: [ [문단1, 문단2, ...] ] 형태
        List<List<String>> contentCol = (List<List<String>>) doc.get("content_col");
        List<String> flatContent = new ArrayList<>();
        if (contentCol != null) {
            contentCol.forEach(flatContent::addAll);
        }

        // ✅ 통합 응답
        Map<String, Object> result = new HashMap<>();
        result.put("articleId", id);
        result.put("title", doc.getString("title"));
        result.put("author", doc.getString("author"));
        result.put("url", doc.getString("url"));
        result.put("publishTime", doc.getString("publish_time"));
        result.put("servingDate", doc.getString("serving_date"));
        result.put("categoryName", doc.get("category_name"));
        result.put("contentCol", flatContent);
        result.put("aiSummary", doc.getString("summary"));
        result.put("aiKeywords", doc.get("keywords"));
        result.put("aiQuizzes", doc.get("quizzes"));
        result.put("source", article.getSource());
        result.put("createdAt", article.getCreatedAt());

        return ResponseEntity.ok(result);
    }

    // ✅ 2️⃣ 오늘 날짜 기준 기사 목록 조회 (MongoDB 기준)
    @GetMapping("/today")
    public ResponseEntity<?> getTodayArticles(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        Query query = new Query(Criteria.where("serving_date").is(targetDate.toString()));
        List<Document> docs = mongoTemplate.find(query, Document.class, "Selected_Articles");

        if (docs.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // ✅ 목록 응답 변환
        List<Map<String, Object>> result = new ArrayList<>();
        for (Document doc : docs) {
            Map<String, Object> item = new HashMap<>();
            item.put("articleId", doc.get("article_id"));
            item.put("title", doc.getString("title"));
            item.put("categoryName", doc.get("category_name"));
            item.put("publishTime", doc.getString("publish_time"));
            item.put("servingDate", doc.getString("serving_date"));
            result.add(item);
        }

        return ResponseEntity.ok(result);
    }
}
