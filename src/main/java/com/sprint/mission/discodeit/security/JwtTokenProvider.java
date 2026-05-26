package com.sprint.mission.discodeit.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sprint.mission.discodeit.config.JwtProperties;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;

  /**
   * Access Token 생성
   */
  public String generateAccessToken(Authentication authentication) {

    Instant now = Instant.now();

    Instant expiration = now.plusSeconds(
        jwtProperties.getAccessTokenExpiration()
    );

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .subject(authentication.getName())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(expiration))
        .claim("type", "access")
        .build();

    return createToken(claimsSet);
  }

  /**
   * Refresh Token 생성
   */
  public String generateRefreshToken(Authentication authentication) {

    Instant now = Instant.now();

    Instant expiration = now.plusSeconds(
        jwtProperties.getRefreshTokenExpiration()
    );

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
        .issueTime(Date.from(now))
        .expirationTime(Date.from(expiration))
        .claim("type", "refresh")
        .build();

    return createToken(claimsSet);
  }

  /**
   * JWT 생성
   */
  private String createToken(JWTClaimsSet claimsSet) {

    try {

      SignedJWT signedJWT = new SignedJWT(
          new JWSHeader.Builder(JWSAlgorithm.HS256)
              .type(JOSEObjectType.JWT)
              .build(),
          claimsSet
      );

      MACSigner signer = new MACSigner(
          jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
      );

      signedJWT.sign(signer);

      return signedJWT.serialize();

    } catch (JOSEException e) {
      throw new RuntimeException("JWT 생성 실패", e);
    }
  }

  /**
   * 토큰 검증
   */
  public boolean validateToken(String token) {

    try {

      SignedJWT signedJWT = SignedJWT.parse(token);

      MACVerifier verifier = new MACVerifier(
          jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
      );

      boolean verified = signedJWT.verify(verifier);

      if (!verified) {
        return false;
      }

      Date expirationTime =
          signedJWT.getJWTClaimsSet().getExpirationTime();

      return expirationTime.after(new Date());

    } catch (ParseException | JOSEException e) {
      return false;
    }
  }

  /**
   * 사용자 ID 추출
   */
  public UUID getUserId(String token) {

    try {

      SignedJWT signedJWT = SignedJWT.parse(token);

      String subject =
          signedJWT.getJWTClaimsSet().getSubject();

      return UUID.fromString(subject);

    } catch (ParseException e) {
      throw new RuntimeException("JWT 파싱 실패", e);
    }
  }
}