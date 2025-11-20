package oba.backend.server.entity.mysql;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "Categories")
public class Category {

    @Id
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name")
    private String categoryName;
}
