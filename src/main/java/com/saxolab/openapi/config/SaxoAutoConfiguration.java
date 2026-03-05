package com.saxolab.openapi.config;

import com.saxolab.openapi.auth.InMemoryTokenStore;
import com.saxolab.openapi.auth.SaxoAuthInterceptor;
import com.saxolab.openapi.auth.SaxoOAuthClient;
import com.saxolab.openapi.auth.SaxoOAuthService;
import com.saxolab.openapi.auth.SaxoOAuthTokenStore;
import com.saxolab.openapi.auth.SaxoTokenProvider;
import com.saxolab.openapi.client.ChartClient;
import com.saxolab.openapi.client.ClosedPositionsClient;
import com.saxolab.openapi.client.PortfolioClient;
import com.saxolab.openapi.client.ReferenceDataClient;
import com.saxolab.openapi.client.RootServicesClient;
import com.saxolab.openapi.client.TradingClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@AutoConfiguration
@EnableConfigurationProperties(SaxoProperties.class)
@EnableScheduling
public class SaxoAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public SaxoOAuthTokenStore saxoOAuthTokenStore() {
    return new InMemoryTokenStore();
  }

  @Bean
  @ConditionalOnMissingBean
  public RestClient saxoAuthRestClient(SaxoProperties properties) {
    return RestClient.builder().baseUrl(properties.getAuthUrl()).build();
  }

  @Bean
  @ConditionalOnMissingBean
  public SaxoOAuthClient saxoOAuthClient(RestClient saxoAuthRestClient, SaxoProperties properties) {
    return new SaxoOAuthClient(saxoAuthRestClient, properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public SaxoOAuthService saxoOAuthService(
      SaxoOAuthClient oauth2Client, SaxoOAuthTokenStore tokenStore, SaxoProperties properties) {
    return new SaxoOAuthService(oauth2Client, tokenStore, properties);
  }

  @Bean
  @ConditionalOnMissingBean
  public SaxoTokenProvider saxoTokenProvider(SaxoOAuthService saxoOAuthService) {
    return saxoOAuthService;
  }

  @Bean
  @ConditionalOnMissingBean
  public SaxoAuthInterceptor saxoAuthInterceptor(SaxoTokenProvider tokenProvider) {
    return new SaxoAuthInterceptor(tokenProvider);
  }

  @Bean
  @ConditionalOnMissingBean(name = "saxoApiRestClient")
  public RestClient saxoApiRestClient(
      SaxoProperties properties, SaxoAuthInterceptor authInterceptor) {
    return RestClient.builder()
        .baseUrl(properties.getBaseUrl())
        .requestInterceptor(authInterceptor)
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public HttpServiceProxyFactory saxoHttpServiceProxyFactory(RestClient saxoApiRestClient) {
    return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(saxoApiRestClient)).build();
  }

  @Bean
  @ConditionalOnMissingBean
  public TradingClient tradingClient(HttpServiceProxyFactory factory) {
    return factory.createClient(TradingClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  public PortfolioClient portfolioClient(HttpServiceProxyFactory factory) {
    return factory.createClient(PortfolioClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  public ReferenceDataClient referenceDataClient(HttpServiceProxyFactory factory) {
    return factory.createClient(ReferenceDataClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  public ChartClient chartClient(HttpServiceProxyFactory factory) {
    return factory.createClient(ChartClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  public RootServicesClient rootServicesClient(HttpServiceProxyFactory factory) {
    return factory.createClient(RootServicesClient.class);
  }

  @Bean
  @ConditionalOnMissingBean
  public ClosedPositionsClient closedPositionsClient(HttpServiceProxyFactory factory) {
    return factory.createClient(ClosedPositionsClient.class);
  }
}
