package oba.backend.server.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    @DisplayName("MongoDB 연결 테스트")
    void testMongoConnection() {
        try {
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("MongoDB 연결 성공: " + dbName);
            assertThat(dbName).isEqualTo("OneBitArticle");
        } catch (Exception e) {
            System.out.println("MongoDB 연결 실패: " + e.getMessage());
            assertThat(e).isNull();
        }
    }
}
