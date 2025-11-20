package oba.backend.server.entity.mysql;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Article_Categories")
public class ArticleCategory {

    @EmbeddedId
    private ArticleCategoryId id;
}
