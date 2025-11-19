package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.entity.BaseEntity;

@Entity
@Getter
@Setter
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

    @Column(name = "quiz1")
    private Boolean quiz1;

    @Column(name = "quiz2")
    private Boolean quiz2;

    @Column(name = "quiz3")
    private Boolean quiz3;

    @Column(name = "quiz4")
    private Boolean quiz4;

    @Column(name = "quiz5")
    private Boolean quiz5;
}
