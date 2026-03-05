package com.saxolab.openapi.auth;

import com.saxolab.openapi.config.SaxoProperties;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class SaxoOAuthService implements SaxoTokenProvider {

  private static final Logger log = LoggerFactory.getLogger(SaxoOAuthService.class);

  private final SaxoOAuthClient oauth2Client;
  private final SaxoOAuthTokenStore tokenStore;
  private final SaxoProperties properties;

  public SaxoOAuthService(
      SaxoOAuthClient oauth2Client, SaxoOAuthTokenStore tokenStore, SaxoProperties properties) {
    this.oauth2Client = oauth2Client;
    this.tokenStore = tokenStore;
    this.properties = properties;
  }

  public String buildAuthorizeUrl(String state) {
    return properties.getAuthUrl()
        + "/authorize?client_id="
        + properties.getAppKey()
        + "&redirect_uri="
        + properties.getRedirectUri()
        + "&response_type=code"
        + "&state="
        + state;
  }

  public void authenticateWithCode(String authorizationCode) {
    OAuthTokenResponse response = oauth2Client.exchangeCode(authorizationCode);
    storeTokenResponse(response);
    log.info("Successfully authenticated with authorization code");
  }

  @Scheduled(fixedDelayString = "${saxo.token-refresh-check-interval-ms:30000}")
  public void refreshTokenIfNeeded() {
    if (!tokenStore.hasTokens()) {
      return;
    }

    Instant expiresAt = tokenStore.getExpiresAt();
    if (expiresAt == null) {
      return;
    }

    Instant refreshThreshold = expiresAt.minusSeconds(properties.getTokenRefreshMarginSeconds());
    if (Instant.now().isAfter(refreshThreshold)) {
      try {
        OAuthTokenResponse response = oauth2Client.refreshToken(tokenStore.getRefreshToken());
        storeTokenResponse(response);
        log.info("Successfully refreshed access token");
      } catch (Exception e) {
        log.error("Failed to refresh access token", e);
      }
    }
  }

  @Override
  public String getAccessToken() {
    return tokenStore.getAccessToken();
  }

  @Override
  public boolean isTokenValid() {
    if (!tokenStore.hasTokens()) {
      return false;
    }
    Instant expiresAt = tokenStore.getExpiresAt();
    return expiresAt != null && Instant.now().isBefore(expiresAt);
  }

  private void storeTokenResponse(OAuthTokenResponse response) {
    Instant expiresAt = Instant.now().plusSeconds(response.expiresIn());
    tokenStore.storeTokens(response.accessToken(), response.refreshToken(), expiresAt);
  }
}
