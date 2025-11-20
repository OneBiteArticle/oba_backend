package oba.backend.server.repository;

import oba.backend.server.domain.gpt.GptResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GptResultRepository extends MongoRepository<GptResult, String> {

    GptResult findTopByOrderByCreatedAtDesc();
}
