package oba.backend.server.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResponseDto {
    private String summary;
    private List<String> keywords;
    private List<QuizItem> quizzes;

    private String result;
}
