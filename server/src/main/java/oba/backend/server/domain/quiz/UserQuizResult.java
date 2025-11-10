package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ UserQuizResult 엔티티
 * - 사용자가 퀴즈를 풀었을 때의 결과(정답/오답)를 MySQL에 저장하는 엔티티
 * - 각 사용자별로 어떤 퀴즈를 풀었고, 어떤 답을 골랐는지, 맞았는지 틀렸는지를 기록함
 * - “사용자 활동 기록” 또는 “채점 결과 테이블” 역할을 담당
 */
@Entity  // JPA 엔티티 지정 → MySQL의 user_quiz_result 테이블로 매핑됨
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuizResult {

    // 🧩 기본 키 (Primary Key)
    // - MySQL에서 AUTO_INCREMENT로 자동 증가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    // 👤 사용자 식별자
    // - MySQL의 User 테이블의 기본키(userId)와 매칭
    // - 어떤 사용자가 퀴즈를 풀었는지 식별 가능
    private Long userId;

    // 🧠 퀴즈 식별자
    // - Quiz 테이블의 quizId를 참조 (외래키 관계)
    // - 어떤 퀴즈를 푼 기록인지 구분
    private Long quizId;

    // ✏️ 사용자가 제출한 답변 (예: "보기2" 또는 "AI")
    private String userAnswer;

    // ✅ 정답 여부 (true: 정답 / false: 오답)
    private boolean isCorrect;

    // 🕒 퀴즈를 푼 시각
    // - 기본값: 현재 시간(LocalDateTime.now())
    private LocalDateTime answeredAt = LocalDateTime.now();
}
