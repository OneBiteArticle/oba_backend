package oba.backend.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOrigins("*")      // 모든 Origin 허용 (필요시 특정 도메인으로 변경 가능)
                .allowedMethods("*")      // GET, POST, PUT, DELETE 모두 허용
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
