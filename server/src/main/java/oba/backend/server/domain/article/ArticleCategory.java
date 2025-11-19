package oba.backend.server.domain.article;

import jakarta.persistence.EmbeddedId;

public class ArticleCategory {

    @EmbeddedId
    private ArticleCategoryId id;
}