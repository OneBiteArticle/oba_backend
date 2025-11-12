package oba.backend.server.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SubmitResultDto {
    private int totalCnt;
    private int correctCnt;
    private int wrongCnt;
}
