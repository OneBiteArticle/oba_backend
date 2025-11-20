package oba.backend.server.repository;

import oba.backend.server.domain.mysql.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {}
