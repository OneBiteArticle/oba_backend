package oba.backend.server.domain.ai;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_news_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiNewsResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @Column(columnDefinition = "JSON")
    private String quizzes;

    @Column(name = "mongo_id", length = 50)
    private String mongoId;
}
