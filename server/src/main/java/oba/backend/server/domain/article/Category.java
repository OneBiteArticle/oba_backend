package oba.backend.server.domain.article;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Categories", schema = "oba_article")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;
}
