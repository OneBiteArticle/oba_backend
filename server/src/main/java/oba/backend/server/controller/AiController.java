package oba.backend.server.controller;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import oba.backend.server.service.AiService;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ AI 관련 REST API Controller
 *
 * 클라이언트(프론트엔드)에서 들어오는 AI 관련 요청을 처리하는 진입점.
 * - /api/ai/analyze : 뉴스 요약 및 퀴즈 생성
 * - /api/ai/generate_news_content : 면접형 뉴스 콘텐츠 생성
 *
 * 실제 비즈니스 로직(AI 서버 호출 등)은 AiService에서 처리.
 */
@RestController  // ✅ REST API 컨트롤러 지정 (JSON 형식으로 응답)
@RequestMapping("/api/ai")  // ✅ 모든 엔드포인트는 /api/ai 경로 하위로 묶임
@RequiredArgsConstructor  // ✅ final 필드(aiService) 자동 생성자 주입
public class AiController {

    // ✅ 실제 AI 처리 로직을 담당하는 서비스 (FastAPI 서버 호출)
    private final AiService aiService;

    /**
     * ✅ 뉴스 분석 및 퀴즈 생성 기능
     *
     * 요청 예시 (POST /api/ai/analyze):
     * {
     *   "title": "OpenAI, GPT-5 출시",
     *   "content": "OpenAI는 새로운 모델 GPT-5를 공개하며 ..."
     * }
     *
     * - 클라이언트가 보낸 AiRequestDto(JSON 요청 바디)를 받고
     * - AiService의 analyzeArticle() 호출
     * - AiResponseDto(JSON 응답) 반환
     */
    @PostMapping("/analyze")
    public AiResponseDto analyze(@RequestBody AiRequestDto request) {
        return aiService.analyzeArticle(request);
    }

    /**
     * ✅ 면접형 뉴스 콘텐츠 생성 기능
     *
     * 요청 예시 (POST /api/ai/generate_news_content):
     * {
     *   "title": "삼성전자, 차세대 AI 반도체 개발 발표",
     *   "content": "삼성전자는 ..."
     * }
     *
     * - AI 서버(FastAPI)에 요청을 전달하여 면접 대비용 뉴스 콘텐츠 생성
     * - AiService.generateNewsContent() 내부에서 WebClient로 FastAPI 호출
     * - 최종적으로 AiResponseDto 형태로 결과 반환
     */
    @PostMapping("/generate_news_content")
    public AiResponseDto generateContent(@RequestBody AiRequestDto request) {
        return aiService.generateNewsContent(request);
    }
}
