package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(
        jwtTokenProvider,
        discodeitUserDetailsService
    );
  }

}
