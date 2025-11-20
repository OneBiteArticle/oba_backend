package oba.backend.server.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArticleSummaryResponse {
    private Long id;
    private String title;
    private List<String> bullets;
}
