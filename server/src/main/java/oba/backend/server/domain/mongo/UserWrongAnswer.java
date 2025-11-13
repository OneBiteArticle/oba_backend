package oba.backend.server.domain.mongo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWrongAnswer {

    // MySQL users.id 값을 참조하는 사용자 고유 식별자
    // 실제 로그인한 사용자(User)의 id 값
    // MongoDB에서는 FK 제약이 없지만, 논리적으로 MySQL의 user.id를 의미함
    private Long userId;

    // NewsResultDocument.quizzes 내 퀴즈를 구분하기 위한 식별 값
    private Long quizId;

    private String userAnswer;

    private String correctAnswer;

    private String explanation;

    private LocalDateTime answeredAt = LocalDateTime.now();
}
