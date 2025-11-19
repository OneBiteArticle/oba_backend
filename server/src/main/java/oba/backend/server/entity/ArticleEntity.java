package oba.backend.server.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ MySQL 기사 메타데이터 (MongoDB ObjectId 연결용)
 */
@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId; // 기본 PK

    // ✅ MongoDB ObjectId 문자열 저장용 (Selected_Articles._id)
    @Column(name = "mongo_id", nullable = false, unique = true)
    private String mongoId;

    @Column(nullable = false)
    private String title;

    private String category;

    private String source; // 원본 출처 (ex. ITWorld, ZDNet 등)

    private LocalDateTime createdAt = LocalDateTime.now();
}
