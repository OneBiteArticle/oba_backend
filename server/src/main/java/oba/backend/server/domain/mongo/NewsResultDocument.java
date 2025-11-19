package oba.backend.server.domain.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "news_results")
public class NewsResultDocument {

    @Id
    private String id;

    // 기사 식별용 ID (MySQL에서 전달받는 articleId)
    // 각 기사를 고유하게 구분하는 키 값
    private Long articleId;

    private String url;

    private String summary;

    private List<String> keywords;

    private List<Map<String, Object>> quizzes;

    // 사용자가 퀴즈를 풀 때 틀린 문제 기록
    // 각 오답은 UserWrongAnswer 객체로 표현
    // 오답 배열 내부에는 userId(MySQL의 user.id), 선택한 답, 문제 ID 등이 들어감
    private List<UserWrongAnswer> wrongAnswers;

    private LocalDateTime createdAt = LocalDateTime.now();
}
