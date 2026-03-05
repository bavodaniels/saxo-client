package com.saxolab.openapi.auth;

import com.saxolab.openapi.config.SaxoProperties;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

public class SaxoOAuthClient {

  private final RestClient authRestClient;
  private final SaxoProperties properties;

  public SaxoOAuthClient(RestClient authRestClient, SaxoProperties properties) {
    this.authRestClient = authRestClient;
    this.properties = properties;
  }

  public OAuthTokenResponse exchangeCode(String authorizationCode) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("code", authorizationCode);
    form.add("client_id", properties.getAppKey());
    form.add("client_secret", properties.getAppSecret());
    form.add("redirect_uri", properties.getRedirectUri());

    return authRestClient
        .post()
        .uri("/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(OAuthTokenResponse.class);
  }

  public OAuthTokenResponse refreshToken(String refreshToken) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "refresh_token");
    form.add("refresh_token", refreshToken);
    form.add("client_id", properties.getAppKey());
    form.add("client_secret", properties.getAppSecret());
    form.add("redirect_uri", properties.getRedirectUri());

    return authRestClient
        .post()
        .uri("/token")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(form)
        .retrieve()
        .body(OAuthTokenResponse.class);
  }
}
