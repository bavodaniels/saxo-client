package com.saxolab.openapi.util;

import org.springframework.web.client.RestClient;

public class TestRestClientFactory {

  public static RestClient createConfiguredRestClient(String baseUrl) {
    return RestClient.builder().baseUrl(baseUrl).build();
  }
}
