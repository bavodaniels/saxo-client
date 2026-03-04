package com.saxolab.openapi.auth;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class SaxoAuthInterceptor implements ClientHttpRequestInterceptor {

    private final SaxoTokenProvider tokenProvider;

    public SaxoAuthInterceptor(SaxoTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String accessToken = tokenProvider.getAccessToken();
        if (accessToken != null) {
            request.getHeaders().setBearerAuth(accessToken);
        }
        return execution.execute(request, body);
    }
}
