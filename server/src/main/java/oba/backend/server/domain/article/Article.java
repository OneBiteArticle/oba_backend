package oba.backend.server.domain.article;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contentCol;

    private LocalDateTime servingDate;

    private String source;
}
