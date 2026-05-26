package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    String authorizationHeader =
        request.getHeader(AUTHORIZATION_HEADER);

    if (authorizationHeader == null ||
        !authorizationHeader.startsWith(BEARER_PREFIX)) {

      filterChain.doFilter(request, response);
      return;
    }

    String accessToken =
        authorizationHeader.substring(BEARER_PREFIX.length());

    if (!jwtTokenProvider.validateToken(accessToken)) {

      filterChain.doFilter(request, response);
      return;
    }

    UUID userId =
        jwtTokenProvider.getUserId(accessToken);

    UserDetails userDetails =
        userDetailsService.loadUserById(userId);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

    SecurityContextHolder
        .getContext()
        .setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}