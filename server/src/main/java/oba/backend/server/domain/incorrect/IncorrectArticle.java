package oba.backend.server.domain.incorrect;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.domain.user.Users;

import java.time.LocalDate;

@Entity
@Table(name = "Incorrect_Articles", schema = "oba_backend")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncorrectArticle {

    @EmbeddedId
    private IncorrectArticleId id;

    @Column(name = "sol_date", nullable = false)
    private LocalDate solDate;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;
}
