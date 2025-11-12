package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.incorrect.IncorrectQuizEntity;
import oba.backend.server.service.IncorrectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incorrect")
@RequiredArgsConstructor
public class IncorrectController {

    private final IncorrectService incorrectService;

    /**
     * ✅ 특정 사용자의 오답 퀴즈 목록 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 오답 퀴즈 리스트
     */
    @GetMapping("/{userId}")
    public List<IncorrectQuizEntity> getUserIncorrectQuizzes(@PathVariable Long userId) {
        return incorrectService.getIncorrectQuizzesByUserId(userId);
    }

    /**
     * ✅ 오답 퀴즈 저장 (사용자가 틀린 문제 등록)
     * @param quiz 오답 퀴즈 엔티티(JSON body로 받음)
     */
    @PostMapping
    public void saveIncorrectQuiz(@RequestBody IncorrectQuizEntity quiz) {
        incorrectService.saveIncorrectQuiz(quiz);
    }

    /**
     * ✅ 오답 퀴즈 전체 삭제 (테스트용, 관리자용)
     */
    @DeleteMapping("/all")
    public void deleteAllIncorrectQuizzes() {
        incorrectService.deleteAllIncorrectQuizzes();
    }
}
