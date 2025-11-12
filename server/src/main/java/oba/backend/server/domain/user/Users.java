package oba.backend.server.domain.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String identifier;
    private String email;
    private String name;
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private ProviderInfo provider;

    // ✅ 생성/수정일자 필드 (선택)
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    // ✅ [핵심] updateInfo() 메서드 추가
    public void updateInfo(String email, String name, String picture) {
        if (email != null) this.email = email;
        if (name != null) this.name = name;
        if (picture != null) this.picture = picture;
    }
}
