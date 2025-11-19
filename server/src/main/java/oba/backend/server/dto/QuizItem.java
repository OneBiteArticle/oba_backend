package oba.backend.server.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizItem {
    private String question;
    private List<String> options;
    private String answer;
    private String explanation;
}
