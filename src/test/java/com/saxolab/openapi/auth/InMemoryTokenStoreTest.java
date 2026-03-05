package com.saxolab.openapi.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTokenStoreTest {

  private InMemoryTokenStore tokenStore;

  @BeforeEach
  void setUp() {
    tokenStore = new InMemoryTokenStore();
  }

  @Test
  void storeTokens_storesAccessToken() {
    String accessToken = "test-access-token";
    String refreshToken = "test-refresh-token";
    Instant expiresAt = Instant.now().plusSeconds(3600);

    tokenStore.storeTokens(accessToken, refreshToken, expiresAt);

    assertThat(tokenStore.getAccessToken()).isEqualTo(accessToken);
  }

  @Test
  void storeTokens_storesRefreshToken() {
    String accessToken = "test-access-token";
    String refreshToken = "test-refresh-token";
    Instant expiresAt = Instant.now().plusSeconds(3600);

    tokenStore.storeTokens(accessToken, refreshToken, expiresAt);

    assertThat(tokenStore.getRefreshToken()).isEqualTo(refreshToken);
  }

  @Test
  void storeTokens_storesExpiresAt() {
    String accessToken = "test-access-token";
    String refreshToken = "test-refresh-token";
    Instant expiresAt = Instant.now().plusSeconds(3600);

    tokenStore.storeTokens(accessToken, refreshToken, expiresAt);

    assertThat(tokenStore.getExpiresAt()).isEqualTo(expiresAt);
  }

  @Test
  void hasTokens_returnsFalseWhenNoTokensStored() {
    assertThat(tokenStore.hasTokens()).isFalse();
  }

  @Test
  void hasTokens_returnsFalseWhenOnlyAccessTokenStored() {
    tokenStore.storeTokens("access-token", null, Instant.now());
    assertThat(tokenStore.hasTokens()).isFalse();
  }

  @Test
  void hasTokens_returnsFalseWhenOnlyRefreshTokenStored() {
    tokenStore.storeTokens(null, "refresh-token", Instant.now());
    assertThat(tokenStore.hasTokens()).isFalse();
  }

  @Test
  void hasTokens_returnsTrueWhenBothTokensStored() {
    tokenStore.storeTokens("access-token", "refresh-token", Instant.now());
    assertThat(tokenStore.hasTokens()).isTrue();
  }

  @Test
  void getAccessToken_returnsNullWhenNotStored() {
    assertThat(tokenStore.getAccessToken()).isNull();
  }

  @Test
  void getRefreshToken_returnsNullWhenNotStored() {
    assertThat(tokenStore.getRefreshToken()).isNull();
  }

  @Test
  void getExpiresAt_returnsNullWhenNotStored() {
    assertThat(tokenStore.getExpiresAt()).isNull();
  }

  @Test
  void storeTokens_overwritesPreviousTokens() {
    String oldAccessToken = "old-access";
    String oldRefreshToken = "old-refresh";
    Instant oldExpiresAt = Instant.now();

    tokenStore.storeTokens(oldAccessToken, oldRefreshToken, oldExpiresAt);
    assertThat(tokenStore.getAccessToken()).isEqualTo(oldAccessToken);

    String newAccessToken = "new-access";
    String newRefreshToken = "new-refresh";
    Instant newExpiresAt = Instant.now().plusSeconds(3600);

    tokenStore.storeTokens(newAccessToken, newRefreshToken, newExpiresAt);

    assertThat(tokenStore.getAccessToken()).isEqualTo(newAccessToken);
    assertThat(tokenStore.getRefreshToken()).isEqualTo(newRefreshToken);
    assertThat(tokenStore.getExpiresAt()).isEqualTo(newExpiresAt);
  }

  @Test
  void concurrentAccess_storesAndRetrievesTokensSafely() throws InterruptedException {
    String accessToken = "concurrent-access";
    String refreshToken = "concurrent-refresh";
    Instant expiresAt = Instant.now();

    Thread writerThread =
        new Thread(
            () -> {
              for (int i = 0; i < 100; i++) {
                tokenStore.storeTokens(accessToken + i, refreshToken + i, expiresAt);
              }
            });

    Thread readerThread =
        new Thread(
            () -> {
              for (int i = 0; i < 100; i++) {
                tokenStore.getAccessToken();
                tokenStore.getRefreshToken();
                tokenStore.getExpiresAt();
                tokenStore.hasTokens();
              }
            });

    writerThread.start();
    readerThread.start();

    writerThread.join();
    readerThread.join();

    // Should complete without deadlock or race condition
    assertThat(tokenStore.getAccessToken()).isNotNull();
  }
}
