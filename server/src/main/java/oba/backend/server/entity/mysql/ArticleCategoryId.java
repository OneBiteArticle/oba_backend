package oba.backend.server.entity.mysql;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@Embeddable
public class ArticleCategoryId implements Serializable {

    private Long articleId;
    private Integer categoryId;
}
