package com.saxolab.openapi.auth;

import com.saxolab.openapi.config.SaxoProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SaxoOAuthServiceTest {

    private MockWebServer mockWebServer;
    private InMemoryTokenStore tokenStore;
    private SaxoProperties properties;
    private SaxoOAuthService service;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        properties = new SaxoProperties();
        properties.setAppKey("key");
        properties.setAppSecret("secret");
        properties.setRedirectUri("https://localhost/cb");
        properties.setAuthUrl(mockWebServer.url("/").toString());
        properties.setTokenRefreshMarginSeconds(60);

        RestClient restClient = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        SaxoOAuthClient oAuthClient = new SaxoOAuthClient(restClient, properties);
        tokenStore = new InMemoryTokenStore();
        service = new SaxoOAuthService(oAuthClient, tokenStore, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void authenticateWithCode_storesTokens() {
        mockWebServer.enqueue(tokenResponse("access-1", "refresh-1", 3600));

        service.authenticateWithCode("code-1");

        assertThat(service.getAccessToken()).isEqualTo("access-1");
        assertThat(service.isTokenValid()).isTrue();
        assertThat(tokenStore.getRefreshToken()).isEqualTo("refresh-1");
    }

    @Test
    void refreshTokenIfNeeded_refreshesWhenCloseToExpiry() {
        // Tokens expire in 30s, within 60s margin
        tokenStore.storeTokens("old-access", "old-refresh", Instant.now().plusSeconds(30));
        mockWebServer.enqueue(tokenResponse("new-access", "new-refresh", 3600));

        service.refreshTokenIfNeeded();

        assertThat(service.getAccessToken()).isEqualTo("new-access");
        assertThat(tokenStore.getRefreshToken()).isEqualTo("new-refresh");
    }

    @Test
    void refreshTokenIfNeeded_doesNotRefreshWhenNotCloseToExpiry() {
        // Tokens expire in 300s, well outside 60s margin
        tokenStore.storeTokens("access", "refresh", Instant.now().plusSeconds(300));

        service.refreshTokenIfNeeded();

        assertThat(mockWebServer.getRequestCount()).isZero();
        assertThat(service.getAccessToken()).isEqualTo("access");
    }

    @Test
    void refreshTokenIfNeeded_doesNothingWhenNoTokensStored() {
        service.refreshTokenIfNeeded();
        assertThat(mockWebServer.getRequestCount()).isZero();
    }

    @Test
    void refreshTokenIfNeeded_handlesRefreshFailureGracefully() {
        tokenStore.storeTokens("access", "refresh", Instant.now().plusSeconds(30));
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("error"));

        service.refreshTokenIfNeeded();

        // Original token still present
        assertThat(service.getAccessToken()).isEqualTo("access");
    }

    @Test
    void isTokenValid_returnsFalseWhenNoTokens() {
        assertThat(service.isTokenValid()).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseWhenExpired() {
        tokenStore.storeTokens("access", "refresh", Instant.now().minusSeconds(10));
        assertThat(service.isTokenValid()).isFalse();
    }

    @Test
    void isTokenValid_returnsTrueWhenNotExpired() {
        tokenStore.storeTokens("access", "refresh", Instant.now().plusSeconds(300));
        assertThat(service.isTokenValid()).isTrue();
    }

    private static MockResponse tokenResponse(String accessToken, String refreshToken, int expiresIn) {
        return new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                          "access_token": "%s",
                          "token_type": "Bearer",
                          "expires_in": %d,
                          "refresh_token": "%s",
                          "refresh_token_expires_in": 86400
                        }
                        """.formatted(accessToken, expiresIn, refreshToken));
    }
}
