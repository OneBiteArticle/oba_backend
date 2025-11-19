package oba.backend.server.dto.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizResultDto {
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;
}
