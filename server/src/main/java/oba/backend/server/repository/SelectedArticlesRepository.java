package oba.backend.server.repository.mongo;

import oba.backend.server.domain.mongo.SelectedArticleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SelectedArticlesRepository extends MongoRepository<SelectedArticleDocument, String> {

    // ✅ 특정 날짜 기준으로 기사 조회
    List<SelectedArticleDocument> findByServingDate(String servingDate);

    // ✅ article_id 기준으로 단일 기사 조회
    SelectedArticleDocument findByArticleId(Long articleId);
}
