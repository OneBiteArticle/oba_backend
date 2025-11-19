package oba.backend.server.domain.quiz;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncorrectArticlesId implements Serializable {
    private Long userId;
    private Long articleId;
}
