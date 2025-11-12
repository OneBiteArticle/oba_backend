package oba.backend.server.domain.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "Selected_Articles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SelectedArticleDocument {
    @Id
    private String id;

    private Long articleId;
    private String url;
    private String title;
    private String author;
    private List<String> categoryName;
    private List<List<String>> contentCol;
    private String publishTime;
    private String servingDate;

    // ✅ AI 결과
    private String aiSummary;
    private List<String> aiKeywords;
    private List<Map<String, Object>> aiQuizzes;
}
