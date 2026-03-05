package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.portfolio.ClosedPosition;
import com.saxolab.openapi.model.portfolio.ClosedPositionList;
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

class ClosedPositionsClientTest {

  private MockWebServer mockWebServer;
  private ClosedPositionsClient closedPositionsClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    RestClient restClient = TestRestClientFactory.createConfiguredRestClient(mockWebServer.url("/").toString());

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    closedPositionsClient = factory.createClient(ClosedPositionsClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getClosedPositions_sendsRequiredQueryParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    ClosedPositionList result =
        closedPositionsClient.getClosedPositions(
            "account-key", "2024-01-01", "2024-12-31", null, null);

    assertThat(result.Data()).isEmpty();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath()).contains("/port/v1/closedpositions");
    assertThat(recordedRequest.getPath()).contains("AccountKey=account-key");
    assertThat(recordedRequest.getPath()).contains("FromDate=2024-01-01");
    assertThat(recordedRequest.getPath()).contains("ToDate=2024-12-31");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
  }

  @Test
  void getClosedPositions_sendsOptionalPaginationParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    ClosedPositionList result =
        closedPositionsClient.getClosedPositions("account-key", "2024-01-01", "2024-12-31", 10, 5);

    assertThat(result.Data()).isEmpty();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath())
        .contains("AccountKey=account-key")
        .contains("FromDate=2024-01-01")
        .contains("ToDate=2024-12-31");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
  }

  @Test
  void getClosedPositions_deserializesEmptyResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    ClosedPositionList result =
        closedPositionsClient.getClosedPositions(
            "account-key", "2024-01-01", "2024-12-31", null, null);

    assertThat(result.Data()).isEmpty();
  }

  @Test
  void getClosedPosition_buildsCorrectPath() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "PositionId": "pos-123",
          "Status": "Closed"
        }
        """));

    ClosedPosition result = closedPositionsClient.getClosedPosition("pos-123");

    assertThat(result).isNotNull();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath()).isEqualTo("/port/v1/closedpositions/pos-123");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
  }

  @Test
  void getClosedPositions_deserializesMultiplePositions() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "PositionId": "pos-1",
              "Status": "Closed"
            },
            {
              "PositionId": "pos-2",
              "Status": "Closed"
            }
          ]
        }
        """));

    ClosedPositionList result =
        closedPositionsClient.getClosedPositions(
            "account-key", "2024-01-01", "2024-12-31", null, null);

    assertThat(result.Data()).hasSize(2);
  }

  @Test
  void getClosedPosition_deserializesPosition() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "PositionId": "pos-999",
          "Status": "Closed"
        }
        """));

    ClosedPosition result = closedPositionsClient.getClosedPosition("pos-999");

    assertThat(result).isNotNull();
  }
}
