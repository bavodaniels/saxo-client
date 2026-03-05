package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.portfolio.AccountList;
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

class PortfolioClientTest {

  private MockWebServer mockWebServer;
  private PortfolioClient portfolioClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    RestClient restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    portfolioClient = factory.createClient(PortfolioClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getAccounts_deserializesJsonResponse() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "AccountId": "123",
              "AccountKey": "key-1",
              "ClientKey": "client-1",
              "AccountType": "Normal",
              "Currency": "USD",
              "Active": true,
              "DisplayName": "My Account"
            }
          ]
        }
        """));

    AccountList result = portfolioClient.getAccounts();

    assertThat(result.Data()).hasSize(1);
    assertThat(result.Data().get(0).AccountId()).isEqualTo("123");
    assertThat(result.Data().get(0).Currency()).isEqualTo("USD");
    assertThat(result.Data().get(0).Active()).isTrue();

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/port/v1/accounts/me");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void getAccounts_handlesEmptyDataArray() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        { "Data": [] }
        """));

    AccountList result = portfolioClient.getAccounts();
    assertThat(result.Data()).isEmpty();
  }

  @Test
  void getAccounts_ignoresUnknownJsonFields() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "AccountId": "1",
              "AccountKey": "k",
              "ClientKey": "c",
              "AccountType": "Normal",
              "Currency": "EUR",
              "Active": false,
              "DisplayName": "Test",
              "SomeUnknownField": "value"
            }
          ],
          "AnotherUnknown": true
        }
        """));

    AccountList result = portfolioClient.getAccounts();
    assertThat(result.Data()).hasSize(1);
    assertThat(result.Data().get(0).AccountId()).isEqualTo("1");
  }

  @Test
  void getAccounts_deserializesMultipleAccounts() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "AccountId": "1",
              "AccountKey": "k1",
              "ClientKey": "c1",
              "AccountType": "Type1",
              "Currency": "USD",
              "Active": true,
              "DisplayName": "Account 1"
            },
            {
              "AccountId": "2",
              "AccountKey": "k2",
              "ClientKey": "c2",
              "AccountType": "Type2",
              "Currency": "EUR",
              "Active": false,
              "DisplayName": "Account 2"
            }
          ]
        }
        """));

    AccountList result = portfolioClient.getAccounts();

    assertThat(result.Data()).hasSize(2);
    assertThat(result.Data().get(0).Currency()).isEqualTo("USD");
    assertThat(result.Data().get(1).Currency()).isEqualTo("EUR");
    assertThat(result.Data().get(0).Active()).isTrue();
    assertThat(result.Data().get(1).Active()).isFalse();
  }
}
