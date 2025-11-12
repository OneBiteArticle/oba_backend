package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.incorrect.IncorrectQuizEntity;
import oba.backend.server.repository.IncorrectQuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncorrectService {

    private final IncorrectQuizRepository incorrectQuizRepository;

    /**
     * ✅ 특정 사용자의 오답 퀴즈 목록 조회
     */
    public List<IncorrectQuizEntity> getIncorrectQuizzesByUserId(Long userId) {
        return incorrectQuizRepository.findByIdUserId(userId);
    }

    /**
     * ✅ 오답 퀴즈 저장
     */
    public void saveIncorrectQuiz(IncorrectQuizEntity quiz) {
        incorrectQuizRepository.save(quiz);
    }

    /**
     * ✅ 오답 퀴즈 전체 삭제
     */
    public void deleteAllIncorrectQuizzes() {
        incorrectQuizRepository.deleteAll();
    }
}
