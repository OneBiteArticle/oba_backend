package oba.backend.server.domain.gpt;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "gpt_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GptResult {

    @Id
    private String id;

    private String date;
    private String title;
    private String summary;
    private List<String> keywords;
    private LocalDateTime createdAt;
}
