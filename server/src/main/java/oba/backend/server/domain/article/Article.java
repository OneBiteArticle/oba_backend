package oba.backend.server.domain.article;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Article {
    @Id
    private Long articleId;

    private String title;
    private String category;
    private String source;

    @Column(columnDefinition = "TEXT")
    private String summary;
}
