package oba.backend.server.domain.incorrect;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.domain.user.Users;  // ✅ 실제 이름이 Users일 경우
import oba.backend.server.entity.ArticleEntity;

@Entity
@Table(name = "incorrect_quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncorrectQuizEntity {

    @EmbeddedId
    private IncorrectQuizId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private Users user;  // ✅ 실제 엔티티 이름 일치시킬 것

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("articleId")
    @JoinColumn(name = "article_id")
    private ArticleEntity article;

    private Boolean quiz1;
    private Boolean quiz2;
    private Boolean quiz3;
    private Boolean quiz4;
    private Boolean quiz5;
}
