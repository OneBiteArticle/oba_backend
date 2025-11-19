package oba.backend.server.scheduler;

import lombok.RequiredArgsConstructor;
import oba.backend.server.service.AiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AiScheduler {

    private final AiService aiService;

    /**
     * 매일 새벽 4시 자동 실행
     * cron 형식: 초 분 시 일 월 요일
     * "0 0 4 * * *" = 매일 00:00:00
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void autoDailyGptUpdate() {
        System.out.println("🔥 [SCHEDULER] Daily GPT Update 실행 시작");
        String result = aiService.runDailyAiJob();
        System.out.println("✅ [SCHEDULER] 실행 완료: " + result);
    }
}
