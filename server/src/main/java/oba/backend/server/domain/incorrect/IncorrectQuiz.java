package oba.backend.server.domain.incorrect;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.domain.user.Users;

@Entity
@Table(name = "Incorrect_Quiz", schema = "oba_backend")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncorrectQuiz {

    @EmbeddedId
    private IncorrectQuizId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Boolean quiz1;

    @Column(nullable = false)
    private Boolean quiz2;

    @Column(nullable = false)
    private Boolean quiz3;

    @Column(nullable = false)
    private Boolean quiz4;

    @Column(nullable = false)
    private Boolean quiz5;
}
