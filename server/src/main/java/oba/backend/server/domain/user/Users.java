package oba.backend.server.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users", schema = "oba_backend")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    private String email;
    private String name;
    private String picture;

    @Enumerated(EnumType.STRING)
    private ProviderInfo provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    public void updateInfo(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.updatedAt = java.time.LocalDateTime.now();
    }
}
