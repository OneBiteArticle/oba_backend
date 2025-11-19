package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Incorrect_Quiz")
@IdClass(IncorrectQuizId.class)
public class IncorrectQuiz {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "article_id")
    private Long articleId;

    private Boolean quiz1;
    private Boolean quiz2;
    private Boolean quiz3;
    private Boolean quiz4;
    private Boolean quiz5;
}
