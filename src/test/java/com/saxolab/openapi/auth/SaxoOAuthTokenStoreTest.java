package com.saxolab.openapi.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

interface SaxoOAuthTokenStoreTest {

  @Test
  default void implementationCanStoreAndRetrieveTokens(SaxoOAuthTokenStore store) {
    String accessToken = "access";
    String refreshToken = "refresh";
    Instant expiresAt = Instant.now();

    store.storeTokens(accessToken, refreshToken, expiresAt);

    assertThat(store.getAccessToken()).isEqualTo(accessToken);
    assertThat(store.getRefreshToken()).isEqualTo(refreshToken);
    assertThat(store.getExpiresAt()).isEqualTo(expiresAt);
  }
}
