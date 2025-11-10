package oba.backend.server.domain.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NewsResultRepository extends MongoRepository<NewsResultDocument, String> {

    Optional<NewsResultDocument> findByArticleId(Long articleId);

    List<NewsResultDocument> findByWrongAnswersUserId(Long userId);
}
