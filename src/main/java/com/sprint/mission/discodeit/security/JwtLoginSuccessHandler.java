package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {

  private static final String REFRESH_TOKEN_COOKIE_NAME =
      "REFRESH_TOKEN";

  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {

    String accessToken =
        jwtTokenProvider.generateAccessToken(authentication);

    String refreshToken =
        jwtTokenProvider.generateRefreshToken(authentication);

    Cookie refreshTokenCookie =
        createRefreshTokenCookie(refreshToken);

    response.addCookie(refreshTokenCookie);

    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) authentication.getPrincipal();

    UserDto userDto = userDetails.getUserDto();

    JwtDto jwtDto = new JwtDto(
        userDto,
        accessToken
    );

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    objectMapper.writeValue(
        response.getWriter(),
        jwtDto
    );
  }

  private Cookie createRefreshTokenCookie(String refreshToken) {

    Cookie cookie = new Cookie(
        REFRESH_TOKEN_COOKIE_NAME,
        refreshToken
    );

    cookie.setHttpOnly(true);

    cookie.setSecure(false);
    // 운영 환경에서는 true 권장

    cookie.setPath("/");

    cookie.setMaxAge(60 * 60 * 24 * 14);

    return cookie;
  }
}