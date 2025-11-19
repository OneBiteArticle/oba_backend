package oba.backend.server.repository;

import oba.backend.server.domain.incorrect.IncorrectQuizEntity;
import oba.backend.server.domain.incorrect.IncorrectQuizId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncorrectQuizRepository extends JpaRepository<IncorrectQuizEntity, IncorrectQuizId> {

    // userId 기반으로 조회
    List<IncorrectQuizEntity> findByIdUserId(Long userId);
}
