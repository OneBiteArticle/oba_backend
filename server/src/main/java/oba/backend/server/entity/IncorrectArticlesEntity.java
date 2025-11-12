package oba.backend.server.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "incorrect_articles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncorrectArticlesEntity {

    @Id
    @Column(name = "id")  // 이미 존재한다면 @GeneratedValue 제거
    private Long id;

    private Long userId;
    private Long articleId;
    private String solDate;
}

