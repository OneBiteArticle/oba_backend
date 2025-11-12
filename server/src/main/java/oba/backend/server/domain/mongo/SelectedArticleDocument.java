package oba.backend.server.domain.mongo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "Selected_Articles")
public class SelectedArticleDocument {

    @Id
    private ObjectId id;

    private Long article_id;
    private String title;
    private List<String> sub_col;      // 소제목 리스트
    private List<List<String>> content_col; // 각 소제목에 해당하는 본문
    private String author;
    private String publish_time;
    private String category_name;
    private String url;
}
