package com.saxolab.openapi.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OAuthTokenResponseTest {

  @Test
  void createTokenResponse_withAllFields() {
    String accessToken = "test-access-token";
    String tokenType = "Bearer";
    int expiresIn = 3600;
    String refreshToken = "test-refresh-token";
    int refreshTokenExpiresIn = 86400;

    OAuthTokenResponse response =
        new OAuthTokenResponse(
            accessToken, tokenType, expiresIn, refreshToken, refreshTokenExpiresIn);

    assertThat(response.accessToken()).isEqualTo(accessToken);
    assertThat(response.tokenType()).isEqualTo(tokenType);
    assertThat(response.expiresIn()).isEqualTo(expiresIn);
    assertThat(response.refreshToken()).isEqualTo(refreshToken);
    assertThat(response.refreshTokenExpiresIn()).isEqualTo(refreshTokenExpiresIn);
  }

  @Test
  void createTokenResponse_withZeroExpiresIn() {
    OAuthTokenResponse response = new OAuthTokenResponse("access", "Bearer", 0, "refresh", 0);

    assertThat(response.expiresIn()).isZero();
    assertThat(response.refreshTokenExpiresIn()).isZero();
  }

  @Test
  void createTokenResponse_withLargeValues() {
    OAuthTokenResponse response =
        new OAuthTokenResponse(
            "very-long-access-token-string",
            "Bearer",
            Integer.MAX_VALUE,
            "very-long-refresh-token-string",
            Integer.MAX_VALUE);

    assertThat(response.expiresIn()).isEqualTo(Integer.MAX_VALUE);
    assertThat(response.refreshTokenExpiresIn()).isEqualTo(Integer.MAX_VALUE);
  }

  @Test
  void createTokenResponse_withNullFields() {
    OAuthTokenResponse response = new OAuthTokenResponse(null, null, 3600, null, 86400);

    assertThat(response.accessToken()).isNull();
    assertThat(response.tokenType()).isNull();
    assertThat(response.refreshToken()).isNull();
  }

  @Test
  void tokenResponse_equality() {
    OAuthTokenResponse response1 =
        new OAuthTokenResponse("access", "Bearer", 3600, "refresh", 86400);
    OAuthTokenResponse response2 =
        new OAuthTokenResponse("access", "Bearer", 3600, "refresh", 86400);

    assertThat(response1).isEqualTo(response2);
  }

  @Test
  void tokenResponse_inequality() {
    OAuthTokenResponse response1 =
        new OAuthTokenResponse("access1", "Bearer", 3600, "refresh", 86400);
    OAuthTokenResponse response2 =
        new OAuthTokenResponse("access2", "Bearer", 3600, "refresh", 86400);

    assertThat(response1).isNotEqualTo(response2);
  }

  @Test
  void tokenResponse_toStringContainsFields() {
    OAuthTokenResponse response =
        new OAuthTokenResponse("access-token", "Bearer", 3600, "refresh-token", 86400);
    String toString = response.toString();

    assertThat(toString).contains("access-token");
    assertThat(toString).contains("Bearer");
    assertThat(toString).contains("3600");
    assertThat(toString).contains("refresh-token");
  }
}
