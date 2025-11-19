package oba.backend.server.domain.user;

import jakarta.persistence.*;
import lombok.*;
import oba.backend.server.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "users")
public class Users extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier; // ✅ CustomOAuth2UserService 에서 사용

    private String email;
    private String name;
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderInfo provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // ✅ 정보 업데이트 메서드 추가
    public void updateInfo(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
}
