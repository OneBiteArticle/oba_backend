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
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")   // ERD와 정확히 매핑
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier; // 예: "google:123456"

    private String email;
    private String name;

    @Column(length = 512)
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderInfo provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** 로그인 시 정보 업데이트 */
    public void updateInfo(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }
}
