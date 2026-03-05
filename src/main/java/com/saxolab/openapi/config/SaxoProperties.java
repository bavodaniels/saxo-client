package com.saxolab.openapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saxo")
public class SaxoProperties {

  private String baseUrl = "https://gateway.saxobank.com/openapi";
  private String authUrl = "https://live.logonvalidation.net";
  private String appKey;
  private String appSecret;
  private String redirectUri;
  private int tokenRefreshMarginSeconds = 60;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getAuthUrl() {
    return authUrl;
  }

  public void setAuthUrl(String authUrl) {
    this.authUrl = authUrl;
  }

  public String getAppKey() {
    return appKey;
  }

  public void setAppKey(String appKey) {
    this.appKey = appKey;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public int getTokenRefreshMarginSeconds() {
    return tokenRefreshMarginSeconds;
  }

  public void setTokenRefreshMarginSeconds(int tokenRefreshMarginSeconds) {
    this.tokenRefreshMarginSeconds = tokenRefreshMarginSeconds;
  }
}
