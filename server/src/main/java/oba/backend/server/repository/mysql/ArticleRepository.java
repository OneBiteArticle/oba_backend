package oba.backend.server.repository.mysql;

import oba.backend.server.entity.mysql.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a WHERE a.isUsed = TRUE ORDER BY a.crawlingTime DESC")
    List<Article> findLatestArticles(Pageable pageable);
}
