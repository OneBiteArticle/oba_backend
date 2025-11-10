package oba.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * ✅ AiResponseDto
 * - FastAPI 서버가 보낸 분석 결과(JSON 응답)를
 *   Spring 서버에서 받기 위한 응답용 DTO 클래스
 *
 * FastAPI → Spring 서버로 오는 JSON 예시 👇
 * {
 *   "summary": "삼성이 차세대 AI 반도체를 공개했다...",
 *   "keywords": ["AI", "반도체", "삼성", "기술혁신", "산업트렌드"],
 *   "quizzes": [
 *     {
 *       "question": "삼성이 공개한 기술은?",
 *       "options": ["AI", "블록체인", "자율주행", "로봇"],
 *       "answer": "AI",
 *       "explanation": "본문에 따르면 AI 반도체를 공개했다고 언급함."
 *     }
 *   ],
 *   "content": "PT형 면접 질문 + 퀴즈 등 완성된 뉴스 콘텐츠",
 *   "result": "OK"
 * }
 */
@Data                   // ✅ Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Builder                // ✅ 빌더 패턴으로 객체 생성 가능
@AllArgsConstructor      // ✅ 모든 필드를 매개변수로 받는 생성자 자동 생성
@NoArgsConstructor       // ✅ 기본 생성자 자동 생성
public class AiResponseDto {

    /**
     * 🧠 GPT가 생성한 뉴스 요약문 (3~5문장)
     * 예: "삼성이 차세대 AI 반도체를 공개하며..."
     */
    private String summary;

    /**
     * 🏷️ 핵심 키워드 목록
     * 예: ["AI", "반도체", "기술혁신", "삼성", "산업"]
     */
    private List<String> keywords;

    /**
     * 🧩 객관식 퀴즈 리스트
     * - 각 퀴즈는 Map<String, Object> 형태로 구성됨
     * - 내부 구조 예시:
     *   {
     *     "question": "문제 내용",
     *     "options": ["보기1", "보기2", "보기3", "보기4"],
     *     "answer": "정답 보기",
     *     "explanation": "해설"
     *   }
     */
    private List<Map<String, Object>> quizzes;

    /**
     * 📰 generate_news_content 응답용 전체 콘텐츠
     * - FastAPI의 면접형 콘텐츠 생성 기능에서 사용됨
     * - 요약, 면접 질문, 객관식 퀴즈 등이 문자열로 한 번에 담김
     */
    private String content;

    /**
     * ⚙️ 요청 처리 상태 메시지
     * - "OK" → 정상 처리됨
     * - "FastAPI /analyze 호출 실패: ..." → 오류 메시지 포함
     */
    private String result;

    /**
     * 🧱 단일 메시지용 생성자 (간단한 응답 반환용)
     * 예: FastAPI 호출 실패 시
     *   return new AiResponseDto("FastAPI 호출 실패");
     */
    public AiResponseDto(String result) {
        this.result = result;
    }
}
