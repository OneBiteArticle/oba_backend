package oba.backend.server.config;

import lombok.RequiredArgsConstructor;
import oba.backend.server.domain.user.UserRepository;
import oba.backend.server.security.CustomAuthorizationRequestResolver;
import oba.backend.server.security.CustomOAuth2UserService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@AutoConfigureAfter(OAuth2ClientAutoConfiguration.class)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private final ClientRegistrationRepository clientRegistrationRepository;

    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationFailureHandler keepSessionFailure =
                (request, response, exception) ->
                        response.sendRedirect("/login?error=" + exception.getClass().getSimpleName());

        CustomAuthorizationRequestResolver customAuthorizationRequestResolver =
                new CustomAuthorizationRequestResolver(clientRegistrationRepository, userRepository);

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(b -> b.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/error",
                                "/oauth2/**", "/css/**", "/js/**",
                                "/images/**", "/img/**",
                                "/api/ai/**",
                                "/mongo-test"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(o -> o
                        .loginPage("/login")
                        .authorizationEndpoint(a -> a.authorizationRequestResolver(customAuthorizationRequestResolver))
                        .defaultSuccessUrl("/login?loggedIn", true)
                        .failureHandler(keepSessionFailure)
                )

                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "refresh_token")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
}
