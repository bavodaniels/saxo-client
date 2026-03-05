package com.saxolab.openapi.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SaxoPropertiesTest {

  private SaxoProperties properties;

  @BeforeEach
  void setUp() {
    properties = new SaxoProperties();
  }

  @Test
  void baseUrl_hasDefaultValue() {
    assertThat(properties.getBaseUrl()).isEqualTo("https://gateway.saxobank.com/openapi");
  }

  @Test
  void authUrl_hasDefaultValue() {
    assertThat(properties.getAuthUrl()).isEqualTo("https://live.logonvalidation.net");
  }

  @Test
  void tokenRefreshMarginSeconds_hasDefaultValue() {
    assertThat(properties.getTokenRefreshMarginSeconds()).isEqualTo(60);
  }

  @Test
  void appKey_isNullByDefault() {
    assertThat(properties.getAppKey()).isNull();
  }

  @Test
  void appSecret_isNullByDefault() {
    assertThat(properties.getAppSecret()).isNull();
  }

  @Test
  void redirectUri_isNullByDefault() {
    assertThat(properties.getRedirectUri()).isNull();
  }

  @Test
  void setBaseUrl_updatesValue() {
    String newBaseUrl = "https://custom.api.com";
    properties.setBaseUrl(newBaseUrl);
    assertThat(properties.getBaseUrl()).isEqualTo(newBaseUrl);
  }

  @Test
  void setAuthUrl_updatesValue() {
    String newAuthUrl = "https://custom-auth.com";
    properties.setAuthUrl(newAuthUrl);
    assertThat(properties.getAuthUrl()).isEqualTo(newAuthUrl);
  }

  @Test
  void setAppKey_updatesValue() {
    String appKey = "test-key-123";
    properties.setAppKey(appKey);
    assertThat(properties.getAppKey()).isEqualTo(appKey);
  }

  @Test
  void setAppSecret_updatesValue() {
    String appSecret = "test-secret-456";
    properties.setAppSecret(appSecret);
    assertThat(properties.getAppSecret()).isEqualTo(appSecret);
  }

  @Test
  void setRedirectUri_updatesValue() {
    String redirectUri = "https://localhost:8080/callback";
    properties.setRedirectUri(redirectUri);
    assertThat(properties.getRedirectUri()).isEqualTo(redirectUri);
  }

  @Test
  void setTokenRefreshMarginSeconds_updatesValue() {
    int margin = 120;
    properties.setTokenRefreshMarginSeconds(margin);
    assertThat(properties.getTokenRefreshMarginSeconds()).isEqualTo(margin);
  }

  @Test
  void setTokenRefreshMarginSeconds_acceptsZero() {
    properties.setTokenRefreshMarginSeconds(0);
    assertThat(properties.getTokenRefreshMarginSeconds()).isZero();
  }

  @Test
  void setTokenRefreshMarginSeconds_acceptsNegative() {
    properties.setTokenRefreshMarginSeconds(-1);
    assertThat(properties.getTokenRefreshMarginSeconds()).isEqualTo(-1);
  }

  @Test
  void multipleSetOperations_appliesChangesSequentially() {
    properties.setAppKey("key1");
    properties.setAppSecret("secret1");
    properties.setRedirectUri("https://localhost/cb1");

    assertThat(properties.getAppKey()).isEqualTo("key1");
    assertThat(properties.getAppSecret()).isEqualTo("secret1");
    assertThat(properties.getRedirectUri()).isEqualTo("https://localhost/cb1");

    properties.setAppKey("key2");
    properties.setBaseUrl("https://new-base.com");

    assertThat(properties.getAppKey()).isEqualTo("key2");
    assertThat(properties.getAppSecret()).isEqualTo("secret1");
    assertThat(properties.getBaseUrl()).isEqualTo("https://new-base.com");
  }

  @Test
  void allPropertiesCanBeSet() {
    String baseUrl = "https://api.example.com";
    String authUrl = "https://auth.example.com";
    String appKey = "key";
    String appSecret = "secret";
    String redirectUri = "https://callback.example.com";
    int tokenRefreshMargin = 30;

    properties.setBaseUrl(baseUrl);
    properties.setAuthUrl(authUrl);
    properties.setAppKey(appKey);
    properties.setAppSecret(appSecret);
    properties.setRedirectUri(redirectUri);
    properties.setTokenRefreshMarginSeconds(tokenRefreshMargin);

    assertThat(properties.getBaseUrl()).isEqualTo(baseUrl);
    assertThat(properties.getAuthUrl()).isEqualTo(authUrl);
    assertThat(properties.getAppKey()).isEqualTo(appKey);
    assertThat(properties.getAppSecret()).isEqualTo(appSecret);
    assertThat(properties.getRedirectUri()).isEqualTo(redirectUri);
    assertThat(properties.getTokenRefreshMarginSeconds()).isEqualTo(tokenRefreshMargin);
  }
}
