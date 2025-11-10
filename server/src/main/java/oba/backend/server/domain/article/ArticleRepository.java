package oba.backend.server.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a WHERE a.servingDate BETWEEN :start AND :end")
    List<Article> findArticlesToProcess(LocalDateTime start, LocalDateTime end);
}
