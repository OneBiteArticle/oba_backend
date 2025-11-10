package oba.backend.server.domain.mongo;

import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ MongoDB 내에서 오답 기록을 관리하기 위한 서브 문서(Sub-document)
 * - 하나의 뉴스 문서(NewsResultDocument) 안에서 특정 사용자의 오답 정보를 표현하는 클래스
 * - 각 오답에는 어떤 사용자가(userId) 어떤 문제(quizId)를 어떻게 틀렸는지 기록됨
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWrongAnswer {

    // ✅ MySQL users.id 값을 참조하는 사용자 고유 식별자
    // - 실제 로그인한 사용자(User)의 id 값
    // - MongoDB에서는 FK 제약이 없지만, 논리적으로 MySQL의 user.id를 의미함
    private Long userId;

    // ✅ 틀린 문제의 ID (퀴즈 식별자)
    // - NewsResultDocument.quizzes 내 퀴즈를 구분하기 위한 식별 값
    private Long quizId;

    // ✅ 사용자가 선택한 답안
    // - 예: "보기2" 또는 "B" 형태
    private String userAnswer;

    // ✅ 정답 (GPT가 생성한 문제의 올바른 답안)
    private String correctAnswer;

    // ✅ 해설 (틀린 문제에 대한 이유나 설명)
    private String explanation;

    // ✅ 사용자가 답변한 시각
    // - 오답이 기록된 시간 자동 저장
    private LocalDateTime answeredAt = LocalDateTime.now();
}
