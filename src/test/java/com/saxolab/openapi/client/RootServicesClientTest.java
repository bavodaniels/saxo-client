package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.root.Session;
import com.saxolab.openapi.model.root.User;
import com.saxolab.openapi.util.TestRestClientFactory;
import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

class RootServicesClientTest {

  private MockWebServer mockWebServer;
  private RootServicesClient rootServicesClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    RestClient restClient = TestRestClientFactory.createConfiguredRestClient(baseUrl);

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    rootServicesClient = factory.createClient(RootServicesClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getSessionCapabilities_callsCorrectEndpoint() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Culture": "en-US",
          "Language": "EN"
        }
        """));

    Session result = rootServicesClient.getSessionCapabilities();

    assertThat(result).isNotNull();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/root/v1/sessions/capabilities");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void getUser_callsCorrectEndpoint() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Name": "John Doe",
          "CustomerId": "123456"
        }
        """));

    User result = rootServicesClient.getUser();

    assertThat(result).isNotNull();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/root/v1/user");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void changeSessionCapabilities_sendsCorrectRequest() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(204));

    rootServicesClient.changeSessionCapabilities();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/root/v1/sessions/capabilities");
    assertThat(request.getMethod()).isEqualTo("PUT");
  }

  @Test
  void getSessionCapabilities_deserializesFullResponse() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Culture": "en-GB",
          "Language": "EN"
        }
        """));

    Session result = rootServicesClient.getSessionCapabilities();

    assertThat(result).isNotNull();
  }

  @Test
  void getUser_deserializesFullResponse() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Name": "Jane Doe",
          "CustomerId": "789012"
        }
        """));

    User result = rootServicesClient.getUser();

    assertThat(result).isNotNull();
  }
}
