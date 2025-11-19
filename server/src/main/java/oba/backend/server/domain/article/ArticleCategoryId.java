package oba.backend.server.domain.article;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ArticleCategoryId implements java.io.Serializable {

    @Column(name = "article_id")
    private Long articleId;

    @Column(name = "category_id")
    private Integer categoryId;
}