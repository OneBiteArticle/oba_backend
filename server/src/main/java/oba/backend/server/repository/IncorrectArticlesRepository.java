package oba.backend.server.repository;

import oba.backend.server.entity.IncorrectArticlesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncorrectArticlesRepository extends JpaRepository<IncorrectArticlesEntity, Long> {

    // z`사용자별 틀린 기사 조회
    List<IncorrectArticlesEntity> findByUserId(Long userId);
}
