package com.saxolab.openapi.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.config.SaxoProperties;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class SaxoOAuthClientTest {

  private MockWebServer mockWebServer;
  private SaxoOAuthClient oAuthClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    SaxoProperties properties = new SaxoProperties();
    properties.setAppKey("test-app-key");
    properties.setAppSecret("test-app-secret");
    properties.setRedirectUri("https://localhost/callback");
    properties.setAuthUrl(mockWebServer.url("/").toString());

    RestClient restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

    oAuthClient = new SaxoOAuthClient(restClient, properties);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void exchangeCode_postsCorrectFormBody() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(tokenResponseJson("access-tok", "refresh-tok", 3600)));

    OAuthTokenResponse response = oAuthClient.exchangeCode("auth-code-123");

    assertThat(response.accessToken()).isEqualTo("access-tok");
    assertThat(response.refreshToken()).isEqualTo("refresh-tok");
    assertThat(response.expiresIn()).isEqualTo(3600);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/token");
    assertThat(request.getHeader("Content-Type")).contains("application/x-www-form-urlencoded");

    Map<String, String> formParams = parseFormBody(request.getBody().readUtf8());
    assertThat(formParams).containsEntry("grant_type", "authorization_code");
    assertThat(formParams).containsEntry("code", "auth-code-123");
    assertThat(formParams).containsEntry("client_id", "test-app-key");
    assertThat(formParams).containsEntry("client_secret", "test-app-secret");
    assertThat(formParams).containsEntry("redirect_uri", "https://localhost/callback");
  }

  @Test
  void refreshToken_postsCorrectFormBody() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(tokenResponseJson("new-access", "new-refresh", 1800)));

    OAuthTokenResponse response = oAuthClient.refreshToken("old-refresh-tok");

    assertThat(response.accessToken()).isEqualTo("new-access");
    assertThat(response.refreshToken()).isEqualTo("new-refresh");
    assertThat(response.expiresIn()).isEqualTo(1800);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getMethod()).isEqualTo("POST");
    assertThat(request.getPath()).isEqualTo("/token");

    Map<String, String> formParams = parseFormBody(request.getBody().readUtf8());
    assertThat(formParams).containsEntry("grant_type", "refresh_token");
    assertThat(formParams).containsEntry("refresh_token", "old-refresh-tok");
    assertThat(formParams).containsEntry("client_id", "test-app-key");
    assertThat(formParams).containsEntry("client_secret", "test-app-secret");
  }

  @Test
  void exchangeCode_deserializesJsonFieldsCorrectly() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "access_token": "at",
          "token_type": "Bearer",
          "expires_in": 7200,
          "refresh_token": "rt",
          "refresh_token_expires_in": 86400
        }
        """));

    OAuthTokenResponse response = oAuthClient.exchangeCode("code");
    assertThat(response.tokenType()).isEqualTo("Bearer");
    assertThat(response.refreshTokenExpiresIn()).isEqualTo(86400);
  }

  private static String tokenResponseJson(String accessToken, String refreshToken, int expiresIn) {
    return """
        {
          "access_token": "%s",
          "token_type": "Bearer",
          "expires_in": %d,
          "refresh_token": "%s",
          "refresh_token_expires_in": 86400
        }
        """
        .formatted(accessToken, expiresIn, refreshToken);
  }

  private static Map<String, String> parseFormBody(String body) {
    return Arrays.stream(body.split("&"))
        .map(p -> p.split("=", 2))
        .collect(
            Collectors.toMap(
                a -> URLDecoder.decode(a[0], StandardCharsets.UTF_8),
                a -> URLDecoder.decode(a[1], StandardCharsets.UTF_8)));
  }
}
