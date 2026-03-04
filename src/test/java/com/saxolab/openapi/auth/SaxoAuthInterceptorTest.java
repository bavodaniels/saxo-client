package com.saxolab.openapi.auth;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SaxoAuthInterceptorTest {

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
    void intercept_addsBearerTokenHeader() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{}"));

        SaxoTokenProvider tokenProvider = new SaxoTokenProvider() {
            @Override
            public String getAccessToken() { return "my-bearer-token"; }
            @Override
            public boolean isTokenValid() { return true; }
        };

        RestClient client = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .requestInterceptor(new SaxoAuthInterceptor(tokenProvider))
                .build();

        client.get().uri("/test").retrieve().body(String.class);

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo("Bearer my-bearer-token");
    }

    @Test
    void intercept_skipsHeaderWhenNoToken() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{}"));

        SaxoTokenProvider tokenProvider = new SaxoTokenProvider() {
            @Override
            public String getAccessToken() { return null; }
            @Override
            public boolean isTokenValid() { return false; }
        };

        RestClient client = RestClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .requestInterceptor(new SaxoAuthInterceptor(tokenProvider))
                .build();

        client.get().uri("/test").retrieve().body(String.class);

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isNull();
    }
}
