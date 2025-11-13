package oba.backend.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
        "oba.backend.server.domain.user",
        "oba.backend.server.domain.article",
        "oba.backend.server.domain.incorrect",
        "oba.backend.server.repository" // ✅ JPA repository 경로 추가
})
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
