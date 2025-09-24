// SecurityConfig.java
package oba.backend.server.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import oba.backend.server.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository repos) throws Exception {
        // 기본 인가요청 리졸버
        DefaultOAuth2AuthorizationRequestResolver baseResolver =
                new DefaultOAuth2AuthorizationRequestResolver(repos, "/oauth2/authorization");

        // 추가 파라미터 주입용 커스텀 리졸버
        OAuth2AuthorizationRequestResolver customResolver = new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = baseResolver.resolve(request);
                if (req == null) return null;
                return customize(request, req);
            }
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest req = baseResolver.resolve(request, clientRegistrationId);
                if (req == null) return null;
                return customize(request, req);
            }
            private OAuth2AuthorizationRequest customize(HttpServletRequest request,
                                                         OAuth2AuthorizationRequest req) {
                String uri = request.getRequestURI();
                String registrationId = uri.substring(uri.lastIndexOf('/') + 1); // google|kakao|naver

                Map<String, Object> params = new LinkedHashMap<>(req.getAdditionalParameters());
                switch (registrationId) {
                    case "google" -> params.put("prompt", "select_account");
                    case "kakao"  -> params.put("prompt", "login");        // 또는 consent
                    case "naver"  -> params.put("auth_type", "reprompt");
                }
                return OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params)
                        .build();
            }
        };

        // 실패해도 세션을 건드리지 않고 /login으로 보냄
        AuthenticationFailureHandler keepSessionFailure =
                (request, response, exception) ->
                        response.sendRedirect("/login?error=" + exception.getClass().getSimpleName());

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(b -> b.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error",
                                "/oauth2/**", "/css/**", "/js/**", "/images/**", "/img/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .loginPage("/login")
                        .authorizationEndpoint(a -> a.authorizationRequestResolver(customResolver))
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .defaultSuccessUrl("/login?loggedIn", true)
                        .failureHandler(keepSessionFailure)
                )
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID","refresh_token")
                        .invalidateHttpSession(true)
                );

        return http.build();

    }
}
