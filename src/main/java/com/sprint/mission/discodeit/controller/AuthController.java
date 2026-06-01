package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.JwtDto;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.InvalidRefreshTokenException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private static final String REFRESH_TOKEN_COOKIE_NAME =
      "REFRESH_TOKEN";

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  private final UserService userService;

  @Override
  @GetMapping("/csrf-token")
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    String tokenValue = csrfToken.getToken();
    log.debug("CSRF 토큰 요청: {}", tokenValue);
    return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION)
        .build();
  }

  @Override
  @PutMapping("/role")
  public ResponseEntity<UserDto> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
    return ResponseEntity.ok(userService.updateRole(request));
  }

  @Override
  @PostMapping("/refresh")
  @ResponseStatus(HttpStatus.OK)
  public JwtDto refresh(
      @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME)
      String refreshToken,

      HttpServletResponse response
  ) {

    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    UUID userId =
        jwtTokenProvider.getUserId(refreshToken);

    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails)
            userDetailsService.loadUserById(userId);

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

    String newAccessToken =
        jwtTokenProvider.generateAccessToken(authentication);

    String newRefreshToken =
        jwtTokenProvider.generateRefreshToken(authentication);

    Cookie cookie =
        createRefreshTokenCookie(newRefreshToken);

    response.addCookie(cookie);

    UserDto userDto = userDetails.getUserDto();

    return new JwtDto(
        userDto,
        newAccessToken
    );
  }

  private Cookie createRefreshTokenCookie(String refreshToken) {

    Cookie cookie = new Cookie(
        REFRESH_TOKEN_COOKIE_NAME,
        refreshToken
    );

    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 14);

    return cookie;
  }
}
