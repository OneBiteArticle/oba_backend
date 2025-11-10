package oba.backend.server.domain.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ✅ GPT가 생성한 뉴스 분석 결과 (요약, 퀴즈, 오답, 해설 등)
 * - FastAPI → Spring 서버로 전달된 AI 분석 결과를 MongoDB에 저장하기 위한 도메인 클래스
 * - MongoDB에서는 ‘문서(Document)’ 형태로 저장되며, 컬렉션 이름은 "news_results"
 * - 하나의 문서(NewsResultDocument)는 하나의 기사(article)에 대한 분석 결과를 의미
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "news_results") // MongoDB 컬렉션 이름 지정
public class NewsResultDocument {

    // ✅ MongoDB에서 자동 생성되는 고유 ID (ObjectId)
    @Id
    private String id;

    // ✅ 기사 식별용 ID (MySQL에서 전달받는 articleId)
    // - 각 기사를 고유하게 구분하는 키 값
    private Long articleId;

    // ✅ 기사 URL (FastAPI 요청 시 전달된 뉴스 링크)
    private String url;

    // ✅ GPT가 생성한 뉴스 요약 내용 (3~5문장 정도)
    private String summary;

    // ✅ GPT가 추출한 주요 키워드 리스트
    // ex) ["AI", "산업", "기술", "시장", "성장"]
    private List<String> keywords;

    // ✅ GPT가 생성한 퀴즈 세트 (JSON 형태로 변환되어 저장)
    // - List<Map<String,Object>> 구조로 다양한 형태의 퀴즈를 유연하게 담을 수 있음
    // ex)
    // [
    //   { "question": "AI란?", "options": ["A","B","C","D"], "answer": "A", "explanation": "인공지능" }
    // ]
    private List<Map<String, Object>> quizzes;

    // ✅ 사용자가 퀴즈를 풀 때 틀린 문제 기록
    // - 각 오답은 UserWrongAnswer 객체로 표현
    // - 오답 배열 내부에는 userId(MySQL의 user.id), 선택한 답, 문제 ID 등이 들어감
    private List<UserWrongAnswer> wrongAnswers;

    // ✅ 문서 생성 시각 (MongoDB에 저장되는 시점 기준)
    private LocalDateTime createdAt = LocalDateTime.now();
}
