package oba.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiResponseDto {

    private String summary;

    private List<String> keywords;

    private List<Map<String, Object>> quizzes;

    private String content;

    private String result;

    public AiResponseDto(String result) {
        this.result = result;
    }
}
