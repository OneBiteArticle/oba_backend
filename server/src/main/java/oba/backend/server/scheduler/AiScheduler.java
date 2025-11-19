package oba.backend.server.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oba.backend.server.service.AiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiScheduler {

    private final AiService aiService;

    /**
     * 매일 00:00 자동 실행
     * 초 분 시 일 월 요일
     * 0 0 0 * * *  = 매일 자정
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDailyGptUpdate() {
        log.info("Daily GPT Update 실행 시작");

        String result = aiService.runDailyAiJob();

        log.info("AI 업데이트 완료: {}", result);
    }
}
