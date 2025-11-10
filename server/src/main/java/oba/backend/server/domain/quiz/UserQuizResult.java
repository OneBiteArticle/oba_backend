package oba.backend.server.domain.quiz;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    // 사용자 식별자
    // MySQL의 User 테이블의 기본키(userId)와 매칭
    // 어떤 사용자가 퀴즈를 풀었는지 식별 가능
    private Long userId;

    // 퀴즈 식별자
    // Quiz 테이블의 quizId를 참조 (외래키 관계)
    // 어떤 퀴즈를 푼 기록인지 구분
    private Long quizId;

    private String userAnswer;

    private boolean isCorrect;

    private LocalDateTime answeredAt = LocalDateTime.now();
}
