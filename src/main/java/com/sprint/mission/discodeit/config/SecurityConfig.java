package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.HttpStatusReturningLogoutSuccessHandler;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;
  private final HttpStatusReturningLogoutSuccessHandler httpStatusReturningLogoutSuccessHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())

        )
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(httpStatusReturningLogoutSuccessHandler)
        )
        .authorizeHttpRequests(auth -> auth
            // CSRF Token 발급
            .requestMatchers("/api/auth/csrf-token").permitAll()

            // 회원가입
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

            // 로그인
            .requestMatchers("/api/auth/login").permitAll()

            // 로그아웃
            .requestMatchers("/api/auth/logout").permitAll()

            // Swagger
            .requestMatchers(
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/webjars/**"
            ).permitAll()

            .requestMatchers(
                "/css/**",
                "/js/**",
                "/images/**"
            ).permitAll()

            // Actuator
            .requestMatchers("/actuator/**").permitAll()

            // API가 아닌 요청 허용
            .requestMatchers(
                "/",
                "/error",
                "/favicon.ico"
            ).permitAll()
            .anyRequest().authenticated()
        );


    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
