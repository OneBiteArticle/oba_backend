package oba.backend.server.domain.article;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ Article 엔티티
 * - 뉴스 기사 정보를 MySQL에 저장하기 위한 JPA 엔티티 클래스
 * - 실제 FastAPI로 분석 요청을 보낼 때 사용되는 기사 URL 또는 텍스트의 "원본 데이터"를 관리함
 * - GPT 분석 결과(news_results)는 MongoDB에 저장되고,
 *   그 원본 기사는 이 Article 엔티티를 통해 RDB(MySQL)에 저장됨
 */
@Entity                     // JPA 엔티티 지정 (테이블 매핑)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    // 🧩 기본 키 (Primary Key)
    // MySQL에서 AUTO_INCREMENT로 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;

    // 📰 기사 제목
    // 예: "삼성, 차세대 AI 반도체 공개"
    private String title;

    // 🧠 기사 본문 내용
    // TEXT 타입으로 지정하여 긴 문자열(뉴스 본문 전체)을 저장 가능
    // columnDefinition = "TEXT" → varchar(255)보다 긴 데이터 허용
    @Column(columnDefinition = "TEXT")
    private String contentCol;

    // 🕒 기사 분석 시점 (GPT 분석 기준 시각)
    // 예: FastAPI가 해당 기사를 분석한 날짜/시간
    private LocalDateTime servingDate;

    // 🗞️ 기사 출처 (선택 필드)
    // 예: "네이버 뉴스", "조선일보", "BBC"
    private String source;
}
