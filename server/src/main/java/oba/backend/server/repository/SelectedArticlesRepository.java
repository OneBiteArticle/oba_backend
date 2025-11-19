package oba.backend.server.repository;

import oba.backend.server.domain.mongo.SelectedArticleDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectedArticlesRepository extends MongoRepository<SelectedArticleDocument, ObjectId> {
}
