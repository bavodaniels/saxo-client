package com.saxolab.openapi.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.auth.InMemoryTokenStore;
import com.saxolab.openapi.auth.SaxoAuthInterceptor;
import com.saxolab.openapi.auth.SaxoOAuthClient;
import com.saxolab.openapi.auth.SaxoOAuthService;
import com.saxolab.openapi.auth.SaxoOAuthTokenStore;
import com.saxolab.openapi.auth.SaxoTokenProvider;
import com.saxolab.openapi.client.ChartClient;
import com.saxolab.openapi.client.PortfolioClient;
import com.saxolab.openapi.client.ReferenceDataClient;
import com.saxolab.openapi.client.RootServicesClient;
import com.saxolab.openapi.client.TradingClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class SaxoAutoConfigurationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SaxoAutoConfiguration.class))
          .withPropertyValues(
              "saxo.app-key=test-key",
              "saxo.app-secret=test-secret",
              "saxo.redirect-uri=https://localhost/callback");

  @Test
  void createsAllBeans() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(SaxoOAuthTokenStore.class);
          assertThat(context).hasSingleBean(SaxoOAuthClient.class);
          assertThat(context).hasSingleBean(SaxoOAuthService.class);
          assertThat(context).hasSingleBean(SaxoTokenProvider.class);
          assertThat(context).hasSingleBean(SaxoAuthInterceptor.class);
          assertThat(context).hasSingleBean(PortfolioClient.class);
          assertThat(context).hasSingleBean(TradingClient.class);
          assertThat(context).hasSingleBean(ReferenceDataClient.class);
          assertThat(context).hasSingleBean(ChartClient.class);
          assertThat(context).hasSingleBean(RootServicesClient.class);
        });
  }

  @Test
  void defaultTokenStoreIsInMemory() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(SaxoOAuthTokenStore.class))
              .isInstanceOf(InMemoryTokenStore.class);
        });
  }

  @Test
  void customTokenStoreOverridesDefault() {
    contextRunner
        .withBean(SaxoOAuthTokenStore.class, () -> new InMemoryTokenStore())
        .run(
            context -> {
              assertThat(context).hasSingleBean(SaxoOAuthTokenStore.class);
            });
  }

  @Test
  void propertiesAreBound() {
    contextRunner
        .withPropertyValues("saxo.base-url=https://custom.api.com")
        .run(
            context -> {
              SaxoProperties props = context.getBean(SaxoProperties.class);
              assertThat(props.getBaseUrl()).isEqualTo("https://custom.api.com");
              assertThat(props.getAppKey()).isEqualTo("test-key");
              assertThat(props.getAppSecret()).isEqualTo("test-secret");
            });
  }

  @Test
  void tokenProviderIsSaxoOAuthService() {
    contextRunner.run(
        context -> {
          assertThat(context.getBean(SaxoTokenProvider.class)).isInstanceOf(SaxoOAuthService.class);
        });
  }
}
