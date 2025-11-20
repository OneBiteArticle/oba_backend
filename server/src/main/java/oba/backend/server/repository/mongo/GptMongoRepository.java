package oba.backend.server.repository.mongo;

import oba.backend.server.domain.mongo.GptDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GptMongoRepository extends MongoRepository<GptDocument, String> {
    Optional<GptDocument> findByArticleId(Long articleId);
}
