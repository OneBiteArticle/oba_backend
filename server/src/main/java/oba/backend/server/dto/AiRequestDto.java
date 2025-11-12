package oba.backend.server.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRequestDto {
    private Long articleId;
    private String url;
}
