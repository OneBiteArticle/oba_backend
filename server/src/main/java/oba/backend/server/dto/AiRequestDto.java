package oba.backend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ✅ AiRequestDto
 * - Spring 서버에서 FastAPI로 요청을 보낼 때 사용하는 데이터 전달용 객체 (DTO)
 * - 주로 기사 분석 요청 시 articleId(기사 ID)와 url(뉴스 주소)을 담아 전송함
 * - JSON 형태로 변환되어 FastAPI로 전달됨
 *
 * 예시 JSON 요청:
 * {
 *   "articleId": 101,
 *   "url": "https://news.naver.com/article/101"
 * }
 */
@Data                   // ✅ Lombok: Getter/Setter, toString(), equals(), hashCode() 자동 생성
@Builder                // ✅ Lombok: 빌더 패턴 지원 → AiRequestDto.builder().articleId(1L).url("...").build()
@AllArgsConstructor      // ✅ 모든 필드를 매개변수로 받는 생성자 자동 생성
@NoArgsConstructor       // ✅ 기본 생성자(매개변수 없는 생성자) 자동 생성
public class AiRequestDto {

    /**
     * 📰 분석할 뉴스의 고유 ID
     * - MySQL의 Article 테이블에서 생성된 articleId 값
     * - MongoDB에서는 이 ID를 기준으로 FastAPI 분석 결과와 연결
     * 예: 101, 102, 103 ...
     */
    private Long articleId;

    /**
     * 🌐 분석할 뉴스 기사 URL
     * - FastAPI가 이 주소를 기반으로 웹 크롤링 수행
     * 예: "https://news.naver.com/article/001/0012345678"
     */
    private String url;
}
