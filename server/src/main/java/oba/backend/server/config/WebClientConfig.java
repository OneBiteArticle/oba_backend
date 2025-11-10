package oba.backend.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ✅ WebClient 전역 설정 클래스
 * <p>
 * Spring WebFlux의 WebClient는 비동기(Non-blocking) HTTP 통신을 지원하는 클라이언트로,
 * 외부 API 서버(FastAPI, Flask, OpenAI API 등)에 요청을 보낼 때 사용됨.
 * <p>
 * 현재 설정에서는 AI 기능 관련 API 서버(FastAPI, EC2 환경)를 대상으로 기본 URL과 헤더를 설정함.
 */
@Configuration  // ✅ Spring 설정 클래스 (Bean 등록 대상)
public class WebClientConfig {

    /**
     * ✅ WebClient Bean 등록
     * <p>
     * - WebClient는 Spring WebFlux가 제공하는 비동기 HTTP 클라이언트
     * - WebClient.Builder를 주입받아 기본 설정을 지정한 뒤 build()하여 Bean으로 등록
     * - 이렇게 등록된 WebClient는 @Autowired 또는 생성자 주입으로 어디서든 재사용 가능
     */
    @Bean
    public WebClient webClㅂient(WebClient.Builder builder) {
        return builder
                // ✅ 1. 기본 요청 URL 설정
                // 모든 요청이 "http://13.125.244.206:8000" (EC2에 띄워진 FastAPI 서버)로 향함
                .baseUrl("http://13.125.244.206:8000")

                // ✅ 2. 기본 헤더 설정 (Content-Type: application/json)
                // 모든 요청의 Content-Type을 JSON으로 지정
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

                // ✅ 3. 설정이 완료된 WebClient 인스턴스 생성 및 반환
                .build();
    }
}
