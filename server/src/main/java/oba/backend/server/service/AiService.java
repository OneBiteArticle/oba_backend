package oba.backend.server.service;

import lombok.RequiredArgsConstructor;
import oba.backend.server.dto.AiRequestDto;
import oba.backend.server.dto.AiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * ✅ AiService
 *
 * Spring 서버와 FastAPI 서버 간의 HTTP 통신을 담당하는 서비스 클래스.
 * - /analyze: 뉴스 요약 및 퀴즈 생성
 * - /generate_news_content: 면접용 뉴스 콘텐츠 생성
 *
 * WebClient를 이용해 비동기 HTTP 요청을 수행하며, FastAPI 응답(JSON)을 AiResponseDto로 변환해 반환함.
 */
@Service
@RequiredArgsConstructor
public class AiService {

    // ✅ WebClient (WebClientConfig에서 전역 Bean으로 등록됨)
    private final WebClient webClient;

    // ✅ application.yml 또는 properties에서 FastAPI 서버 URL 주입
    // 예: ai.server.url=http://13.125.244.206:8000
    @Value("${ai.server.url}")
    private String aiServerUrl;

    // ----------------------------------------------------------------------
    // 🧠 1️⃣ 뉴스 요약 + 퀴즈 생성 요청 (FastAPI /analyze 엔드포인트)
    // ----------------------------------------------------------------------
    public AiResponseDto analyzeArticle(AiRequestDto request) {
        try {
            // ✅ 요청 로그 출력
            System.out.println("🚀 FastAPI [/analyze] 요청 시작: " + request.getUrl());

            // ✅ WebClient를 이용해 외부 서버인 FastAPI에 POST 요청 전송!!!
            return webClient.post()
                    .uri(aiServerUrl + "/analyze")  // FastAPI의 /analyze 엔드포인트로 요청
                    .bodyValue(request)              // AiRequestDto → JSON 자동 변환, 여기서 request는 AiRequestDto 객체죠 👇, 즉, bodyValue(request) = “이 객체를 HTTP 요청 본문(body) 에 넣어서 FastAPI로 보내라”
                    .retrieve()                      // HTTP 응답 수신 (상태 코드 4xx, 5xx는 예외 발생)
                    .bodyToMono(AiResponseDto.class) // 응답(JSON)을 AiResponseDto 객체로 매핑 , FastAPI 응답을 Java 객체로 변환, FastAPI는 처리 후 이런 JSON을 돌려줍니다.

                    // ✅ 예외 발생 시 기본 응답으로 대체
                    .onErrorResume(e -> {
                        System.err.println("❌ [/analyze] 호출 실패: " + e.getMessage());
                        return Mono.just(AiResponseDto.builder()
                                .result("FastAPI /analyze 호출 실패: " + e.getMessage())
                                .build());
                    })
                    // ✅ block()을 호출하여 비동기 결과를 동기식으로 반환
                    .block(); // 여기서 Mono는 Reactor의 비동기 데이터 타입이에요. 쉽게 말하면, “응답이 오면 나중에 알려줄게” 라는 약속(promise) 같은 객체입니다.
                              // 하지만 지금 AiService는 즉시 응답 결과를 받아야 하잖아요? 그래서 .block()을 붙여서 “응답이 올 때까지 기다렸다가 결과를 바로 받아와라” 라고 명령하는 거예요.

        } catch (Exception e) {
            // ✅ 예외 발생 시 로그 출력 및 기본 응답 반환
            System.err.println("🚨 [/analyze] 예외 발생: " + e.getMessage());
            return AiResponseDto.builder()
                    .result("FastAPI /analyze 예외 발생: " + e.getMessage())
                    .build();
        }
    }

    // ----------------------------------------------------------------------
    // 🧩 2️⃣ 뉴스 기반 면접형 콘텐츠 생성 요청 (FastAPI /generate_news_content)
    // ----------------------------------------------------------------------
    /*generateNewsContent()는
    클라이언트로부터 받은 AiRequestDto를 FastAPI로 보내고,
    FastAPI의 응답(JSON)을 AiResponseDto로 변환해
    동기적으로 반환하는 중간 통신 메서드입니다.*/

    public AiResponseDto generateNewsContent(AiRequestDto request) {
        try {
            System.out.println("🚀 FastAPI [/generate_news_content] 요청 시작: " + request.getUrl());

            // ✅ WebClient를 이용해 외부 서버(FastAPI)에 POST 요청 전송
            // - FastAPI의 /generate_news_content 엔드포인트로 요청을 보냄
            // - WebClient는 비동기 HTTP 클라이언트지만, 아래에서 block()을 사용해 동기식으로 변환
            return webClient.post()
                    .uri(aiServerUrl + "/generate_news_content") // FastAPI의 /generate_news_content 호출
                    .bodyValue(request)                          // AiRequestDto → JSON 변환 (HTTP Body에 담김)
                    // 예시 전송 JSON:
                    // {
                    //   "articleId": 101,
                    //   "url": "https://news.naver.com/article/001/0012345678"
                    // }

                    .retrieve()                                 // HTTP 응답 수신 (FastAPI로부터 응답 기다림)
                    // - 상태 코드가 4xx 또는 5xx인 경우 예외 발생
                    .bodyToMono(AiResponseDto.class)            // 응답(JSON)을 AiResponseDto로 역직렬화(매핑)
                    // bodyToMono(AiResponseDto.class) — 이 한 줄은 FastAPI가 반환한 JSON 응답을 자바 객체(AiResponseDto)로 자동 변환(역직렬화) 하는 단계입니다.
                    //한 문장으로 말하면 👇
                    //“FastAPI가 보낸 JSON 데이터를 AiResponseDto 형태의 Java 객체로 바꿔줘.”

                    // 예시 응답 JSON:
                    // {
                    //   "url": "...",
                    //   "content": "요약 + 면접 질문 + 객관식 퀴즈",
                    //   "result": "OK"
                    // }
                    // → Java 객체로 변환되어 반환됨

                    // ✅ 예외 발생 시 기본 응답 객체로 대체
                    // - FastAPI 서버가 꺼져 있거나 네트워크 오류 발생 시
                    // - onErrorResume()이 실행되어 "기본 AiResponseDto"를 반환
                    .onErrorResume(e -> {
                        System.err.println("❌ [/generate_news_content] 호출 실패: " + e.getMessage());
                        return Mono.just(AiResponseDto.builder()
                                .result("FastAPI /generate_news_content 호출 실패: " + e.getMessage())
                                .build());
                    })

                    // ✅ block() 호출: 비동기 → 동기식 전환
                    // - Mono는 Reactor의 비동기 데이터 타입 (응답이 오면 “나중에 알려줄게” 형태)
                    // - block()을 통해 응답이 도착할 때까지 대기 → 결과를 즉시 반환
                    .block();

        } catch (Exception e) {
            // ✅ 예외 발생 시 로그 출력 및 기본 응답 반환
            // - WebClient 내부에서 발생하지 않은 예외(NullPointerException, JSON 파싱 오류 등)를 처리
            // - FastAPI와의 통신 실패 시 에러 메시지를 포함한 AiResponseDto를 반환
            System.err.println("🚨 [/generate_news_content] 예외 발생: " + e.getMessage());
            return AiResponseDto.builder()
                    .result("FastAPI /generate_news_content 예외 발생: " + e.getMessage())
                    .build();
        }
    }


    // ----------------------------------------------------------------------
    // ✅ 3️⃣ 공통 Wrapper 메서드
    //    - 외부에서 callPythonServer()로 접근하면 내부적으로 generateNewsContent() 실행
    // ----------------------------------------------------------------------
//    AiRequestDto request를 받아서
//    내부적으로 generateNewsContent(request)를 호출하고
//    그 결과(AiResponseDto)를 그대로 반환

//    callPythonServer()는 generateNewsContent()를 그대로 호출하는 래퍼 메서드로,
//    “Spring이 Python(FastAPI) 서버에 요청을 보낸다”는 의도를 명확히 드러내기 위해 존재
    public AiResponseDto callPythonServer(AiRequestDto request) {
        return generateNewsContent(request);
    }
}
