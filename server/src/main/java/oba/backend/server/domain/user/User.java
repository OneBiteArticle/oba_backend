package oba.backend.server.domain.user;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String identifier;
    private String email;
    private String name;

    @Column(length = 512)
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ProviderInfo provider;

    public void updateInfo(String email, String name, String picture) {
        if (email != null) this.email = email;
        if (name != null) this.name = name;
        if (picture != null) this.picture = picture;
    }
}
