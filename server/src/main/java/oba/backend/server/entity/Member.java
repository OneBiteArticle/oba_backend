package oba.backend.server.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 'username' 대신 'email'을 사용하고, 고유해야 합니다.
    @Column(unique = true, nullable = false)
    private String email;

    // 화면에 표시될 닉네임을 추가합니다.
    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String refreshToken;

    @Column(nullable = false)
    private boolean isWithdrawn = false;

    @Builder
    public Member(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isWithdrawn = false;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void withdraw() {
        this.isWithdrawn = true;
        this.refreshToken = null;
    }
}
