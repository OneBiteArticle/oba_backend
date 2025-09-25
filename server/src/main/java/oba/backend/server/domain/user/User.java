package oba.backend.server.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users", indexes = {
        @Index(name = "uk_users_identifier", columnList = "identifier", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 고유 식별자: "provider:providerUserId"
     * 예) "kakao:123456", "naver:AbCdE", "google:1081..."
     */
    @Column(nullable = false, unique = true, length = 128)
    private String identifier;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ProviderInfo provider;

    private String email;
    private String name;
    private String picture;

    public void updateInfo(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
}
