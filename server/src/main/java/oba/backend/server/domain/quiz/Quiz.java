package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    // 기사 식별자 (어떤 기사에서 만들어진 퀴즈인지 구분)
    // MongoDB의 NewsResultDocument.articleId 와 동일한 값 사용
    private Long articleId;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "JSON")
    private String options;

    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    private LocalDateTime createdAt = LocalDateTime.now();
}
