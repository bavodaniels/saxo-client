package com.saxolab.openapi.auth;

import java.time.Instant;

public interface SaxoOAuthTokenStore {

  void storeTokens(String accessToken, String refreshToken, Instant expiresAt);

  String getAccessToken();

  String getRefreshToken();

  Instant getExpiresAt();

  boolean hasTokens();
}
