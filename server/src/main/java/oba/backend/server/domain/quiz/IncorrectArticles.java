package oba.backend.server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.entity.BaseEntity;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Incorrect_Articles")
@IdClass(IncorrectArticlesId.class)
public class IncorrectArticles {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "sol_date")
    private java.time.LocalDateTime solDate;
}
