package oba.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {

    private String summary;
    private List<KeywordItem> keywords;
    private List<QuizItem> quizzes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordItem {
        private String word;
        private String definition;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizItem {
        private String question;
        private List<String> options;
        private String answer;
        private String explanation;
    }
}
