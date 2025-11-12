package oba.backend.server.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitResultDto {
    private String message;     // 결과 메시지
    private int correctCount;   // 맞은 문제 수
    private int totalCount;     // 전체 문제 수
}
