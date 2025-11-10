package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ Quiz 엔티티
 * - GPT가 생성한 뉴스 기반 퀴즈(문제, 보기, 정답, 해설 등)를 MySQL DB에 저장하는 클래스
 * - 하나의 기사(articleId) 당 여러 개의 Quiz 엔티티가 생성될 수 있음
 * - 즉, “기사 1개 → 퀴즈 N개” 구조
 */
@Entity // JPA 엔티티 지정 → MySQL의 quiz 테이블로 매핑됨
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    // ✅ 기본 키 (Primary Key)
    // - MySQL에서 AUTO_INCREMENT로 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    // ✅ 기사 식별자 (어떤 기사에서 만들어진 퀴즈인지 구분)
    // - MongoDB의 NewsResultDocument.articleId 와 동일한 값 사용
    private Long articleId;

    // ✅ 문제 내용 (GPT가 생성한 퀴즈 질문)
    // - TEXT 타입: 긴 문자열 저장 가능
    @Column(columnDefinition = "TEXT")
    private String question;

    // ✅ 보기 목록 (객관식 보기 4개 등)
    // - JSON 문자열 형태로 저장
    // - 예: ["AI", "블록체인", "빅데이터", "클라우드"]
    // - 프론트엔드에서 파싱해서 렌더링함
    @Column(columnDefinition = "JSON")
    private String options;

    // ✅ 정답 (예: "AI" 또는 "보기1")
    private String correctAnswer;

    // ✅ 해설 (정답의 이유 또는 추가 설명)
    @Column(columnDefinition = "TEXT")
    private String explanation;

    // ✅ 요약 (문제가 생성된 뉴스의 핵심 요약)
    // - FastAPI → GPT 응답에서 함께 제공됨
    @Column(columnDefinition = "TEXT")
    private String summary;

    // ✅ 키워드 (뉴스에서 추출된 주요 단어)
    // - 예: "AI, 기술, 혁신, 산업"
    @Column(columnDefinition = "TEXT")
    private String keywords;

    // ✅ 생성 시각 (자동으로 현재 시각 기록)
    private LocalDateTime createdAt = LocalDateTime.now();
}
