package oba.backend.server.entity.mysql;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "Articles")
public class Article {

    @Id
    @Column(name = "article_id")
    private Long articleId;

    private String url;

    @Column(name = "crawling_time")
    private LocalDateTime crawlingTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @Column(name = "dup_cnt")
    private Integer dupCnt;

    private BigDecimal ordering;

    @Column(name = "is_used")
    private Boolean isUsed;
}
