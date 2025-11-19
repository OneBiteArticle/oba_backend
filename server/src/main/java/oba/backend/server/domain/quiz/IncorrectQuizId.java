package oba.backend.server.domain.quiz;

import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class IncorrectQuizId implements Serializable {

    private Long userId;
    private Long articleId;
}
