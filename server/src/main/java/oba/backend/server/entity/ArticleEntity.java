package oba.backend.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "articles")
public class ArticleEntity {

    @Id
    @Column(name = "article_id")
    private Long articleId;

    private String title;
    private String category;
    private String source;
    private LocalDateTime createdAt;
}
