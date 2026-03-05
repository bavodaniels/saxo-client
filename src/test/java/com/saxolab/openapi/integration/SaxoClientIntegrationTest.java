package com.saxolab.openapi.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.auth.SaxoOAuthService;
import com.saxolab.openapi.auth.SaxoTokenProvider;
import com.saxolab.openapi.config.SaxoAutoConfiguration;
import com.saxolab.openapi.config.SaxoProperties;
import java.io.IOException;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SaxoClientIntegrationTest {

  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void saxoAutoConfiguration_createsAllBeansWithCustomProperties() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaxoAutoConfiguration.class))
            .withPropertyValues(
                "saxo.base-url=" + mockWebServer.url("/"),
                "saxo.auth-url=" + mockWebServer.url("/"),
                "saxo.app-key=test-key",
                "saxo.app-secret=test-secret",
                "saxo.redirect-uri=https://localhost/callback",
                "saxo.token-refresh-margin-seconds=120");

    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(SaxoProperties.class);
          assertThat(context).hasSingleBean(SaxoOAuthService.class);
          assertThat(context).hasSingleBean(SaxoTokenProvider.class);

          SaxoProperties props = context.getBean(SaxoProperties.class);
          assertThat(props.getAppKey()).isEqualTo("test-key");
          assertThat(props.getTokenRefreshMarginSeconds()).isEqualTo(120);
        });
  }

  @Test
  void saxoAutoConfiguration_usesDefaultPropertiesWhenNotProvided() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaxoAutoConfiguration.class))
            .withPropertyValues(
                "saxo.app-key=key",
                "saxo.app-secret=secret",
                "saxo.redirect-uri=https://localhost/callback");

    contextRunner.run(
        context -> {
          SaxoProperties props = context.getBean(SaxoProperties.class);
          assertThat(props.getBaseUrl()).isEqualTo("https://gateway.saxobank.com/openapi");
          assertThat(props.getAuthUrl()).isEqualTo("https://live.logonvalidation.net");
          assertThat(props.getTokenRefreshMarginSeconds()).isEqualTo(60);
        });
  }

  @Test
  void tokenProviderBecomesOAuthService() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaxoAutoConfiguration.class))
            .withPropertyValues(
                "saxo.app-key=key",
                "saxo.app-secret=secret",
                "saxo.redirect-uri=https://localhost/callback");

    contextRunner.run(
        context -> {
          SaxoTokenProvider tokenProvider = context.getBean(SaxoTokenProvider.class);
          SaxoOAuthService oauthService = context.getBean(SaxoOAuthService.class);
          assertThat(tokenProvider).isInstanceOf(SaxoOAuthService.class);
          assertThat(tokenProvider).isEqualTo(oauthService);
        });
  }

  @Test
  void propertiesCanBeOverriddenByEnvironment() {
    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaxoAutoConfiguration.class))
            .withPropertyValues(
                "saxo.base-url=https://custom.api.com",
                "saxo.auth-url=https://custom-auth.com",
                "saxo.app-key=custom-key",
                "saxo.app-secret=custom-secret",
                "saxo.redirect-uri=https://custom-redirect.com",
                "saxo.token-refresh-margin-seconds=180");

    contextRunner.run(
        context -> {
          SaxoProperties props = context.getBean(SaxoProperties.class);
          assertThat(props.getBaseUrl()).isEqualTo("https://custom.api.com");
          assertThat(props.getAuthUrl()).isEqualTo("https://custom-auth.com");
          assertThat(props.getAppKey()).isEqualTo("custom-key");
          assertThat(props.getAppSecret()).isEqualTo("custom-secret");
          assertThat(props.getRedirectUri()).isEqualTo("https://custom-redirect.com");
          assertThat(props.getTokenRefreshMarginSeconds()).isEqualTo(180);
        });
  }
}
