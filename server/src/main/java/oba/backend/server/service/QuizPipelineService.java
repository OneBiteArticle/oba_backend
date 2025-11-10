package oba.backend.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.quiz.Quiz;
import oba.backend.server.domain.quiz.QuizRepository;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ✅ QuizPipelineService
 * - 매일 자동으로 뉴스 기사를 선택하고 FastAPI에 전달하여
 *   GPT가 퀴즈를 생성하게 한 뒤, 그 결과를 MySQL DB에 저장하는 “자동 퀴즈 생성 파이프라인”
 *
 * 🧩 주요 흐름:
 *   1️⃣ 오늘 날짜의 기사 목록을 DB(Selected_Articles 테이블)에서 조회
 *   2️⃣ 각 기사(URL)를 FastAPI로 전송하여 요약 및 퀴즈 생성 요청
 *   3️⃣ FastAPI의 응답(JSON)을 AiResponseDto로 수신
 *   4️⃣ 응답에서 각 퀴즈를 Quiz 엔티티로 변환하여 MySQL에 저장
 *
 * 즉, “뉴스 데이터 → GPT 분석 → 퀴즈 데이터 저장”을 자동화한 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class QuizPipelineService {

    // ✅ JdbcTemplate: SQL을 직접 실행할 수 있는 JDBC 헬퍼
    // 여기서는 "Selected_Articles" 테이블에서 기사 목록을 가져오기 위해 사용
    private final JdbcTemplate jdbcTemplate;

    // ✅ FastAPI 호출용 서비스 (AiService)
    // GPT가 뉴스 요약 + 퀴즈를 생성하는 API를 호출하는 역할
    private final AiService aiService;

    // ✅ 퀴즈 저장용 JPA Repository
    // 생성된 퀴즈를 MySQL의 quiz 테이블에 저장하는 데 사용
    private final QuizRepository quizRepository;

    // ✅ Jackson의 ObjectMapper: Java 객체 ↔ JSON 문자열 변환
    // 보기(options)나 키워드 리스트를 문자열로 DB에 저장할 때 사용
    private final ObjectMapper mapper = new ObjectMapper();

    // ----------------------------------------------------------------------
    // 🧩 퀴즈 생성 파이프라인 실행 (하루치 기사 대상)
    // ----------------------------------------------------------------------
    /**
     * 오늘 날짜의 기사를 기준으로 FastAPI를 호출하여
     * 각 뉴스별 퀴즈를 자동 생성하고 MySQL DB에 저장하는 메서드.
     * (스케줄러나 관리자 요청에 의해 실행 가능)
     */
    @Transactional
    public void generateQuizzesFromSelectedArticles() {

        // ✅ 1️⃣ 오늘 날짜 기준 기사 목록 조회 (데이터팀이 저장한 테이블)
        // - Selected_Articles: “오늘 처리할 뉴스 기사 목록”이 저장된 테이블
        String sql = "SELECT article_id, url FROM Selected_Articles WHERE serving_date = CURDATE()";

        // jdbcTemplate으로 쿼리 실행 → 각 행을 Map<String, Object>로 받아 리스트에 저장
        List<Map<String, Object>> articles = jdbcTemplate.queryForList(sql);

        // ✅ 2️⃣ 조회된 기사들 순회 처리
        for (Map<String, Object> row : articles) {

            // (1) 컬럼 추출 (article_id, url)
            Long articleId = ((Number) row.get("article_id")).longValue();
            String url = (String) row.get("url");

            System.out.println("📰 기사 처리 중: " + url);

            // ✅ 3️⃣ FastAPI에 요청 보낼 DTO 생성
            AiRequestDto request = AiRequestDto.builder()
                    .articleId(articleId)
                    .url(url)
                    .build();

            // ✅ 4️⃣ FastAPI 호출 (GPT 분석 요청)
            // - aiService.callPythonServer() → FastAPI의 /generate_news_content 엔드포인트 호출
            // - 응답은 AiResponseDto 형태 (summary, quizzes, keywords 등 포함)
            AiResponseDto response = aiService.callPythonServer(request);

            // ✅ 5️⃣ FastAPI 응답 검증
            if (response == null || response.getQuizzes() == null) {
                System.err.println("❌ GPT 응답이 비정상입니다: " + url);
                continue; // 해당 기사 건너뛰기
            }

            // ✅ 6️⃣ GPT 응답 내 퀴즈 리스트 순회하며 DB 저장
            // “이미 정의된 quiz 테이블에 넣을 한 줄 데이터를 객체로 만드는 코드”예요.
            for (Map<String, Object> quizMap : response.getQuizzes()) {
                try {
                    // (1) JSON 응답을 Quiz 엔티티로 변환
                    Quiz quiz = Quiz.builder()
                            .articleId(articleId) // 어떤 기사에 속한 퀴즈인지 표시
                            .question((String) quizMap.get("question")) // 문제 내용
                            .options(mapper.writeValueAsString(quizMap.get("options"))) // 보기(JSON 배열) → 문자열로 저장
                            .correctAnswer((String) quizMap.get("answer")) // 정답
                            .explanation((String) quizMap.get("explanation")) // 해설
                            .summary(response.getSummary()) // 뉴스 요약
                            .keywords(mapper.writeValueAsString(response.getKeywords())) // 키워드 리스트 → 문자열
                            .build();

                    // (2) MySQL DB에 저장
                    quizRepository.save(quiz);

                } catch (Exception e) {
                    // ⚠️ 개별 퀴즈 저장 실패 시 로깅만 하고 다음 퀴즈로 진행
                    System.err.println("⚠️ 퀴즈 저장 실패 (" + url + "): " + e.getMessage());
                }
            }

            // ✅ 7️⃣ 해당 기사 퀴즈 저장 완료 로그
            System.out.println("✅ " + url + " 퀴즈 저장 완료");
        }
    }
}
