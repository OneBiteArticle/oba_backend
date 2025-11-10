package oba.backend.server.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserQuizResultRepository extends JpaRepository<UserQuizResult, Long> {
}