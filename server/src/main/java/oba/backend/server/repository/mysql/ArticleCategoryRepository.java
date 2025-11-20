package oba.backend.server.repository.mysql;

import oba.backend.server.entity.mysql.ArticleCategory;
import oba.backend.server.entity.mysql.ArticleCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, ArticleCategoryId> {
    List<ArticleCategory> findByIdArticleId(Long articleId);
}
