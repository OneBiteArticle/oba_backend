package oba.backend.server.dto.quiz;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SubmitRequestDto {
    private Long userId;
    private Long articleId;
    private List<QuizResultDto> results;
}
