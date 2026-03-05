package com.saxolab.openapi.util;

import org.springframework.web.client.RestClient;

public class TestRestClientFactory {

  public static RestClient createConfiguredRestClient(String baseUrl) {
    // Create a basic RestClient with the base URL
    // The application.properties configuration will handle Jackson settings
    // when the app runs with Spring Boot context
    return RestClient.builder().baseUrl(baseUrl).build();
  }
}
