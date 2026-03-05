package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.chart.ChartData;
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

class ChartClientTest {

  private MockWebServer mockWebServer;
  private ChartClient chartClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    RestClient restClient = TestRestClientFactory.createConfiguredRestClient(mockWebServer.url("/").toString());

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    chartClient = factory.createClient(ChartClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void getChartData_sendsCorrectQueryParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Candles": []
        }
        """));

    chartClient.getChartData(100001, "Stock", 1, 100);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).contains("/chart/v1/charts");
    assertThat(request.getPath()).contains("Uic=100001");
    assertThat(request.getPath()).contains("AssetType=Stock");
    assertThat(request.getPath()).contains("Horizon=1");
    assertThat(request.getPath()).contains("Count=100");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void getChartData_deserializesEmptyResponse() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [],
          "DataVersion": 1
        }
        """));

    ChartData result = chartClient.getChartData(100001, "FxSpot", 5, 50);

    assertThat(result.Data()).isEmpty();
  }

  @Test
  void getChartData_deserializesResponseWithCandles() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "OpenAsk": 1.2000,
              "CloseAsk": 1.2050,
              "HighAsk": 1.2100,
              "LowAsk": 1.1950,
              "Count": 5,
              "IsComplete": true
            }
          ],
          "DataVersion": 1
        }
        """));

    ChartData result = chartClient.getChartData(220, "FxSpot", 1, 10);

    assertThat(result.Data()).hasSize(1);
  }

  @Test
  void getChartData_deserializesMultipleCandles() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "OpenAsk": 1.2000,
              "CloseAsk": 1.2050,
              "HighAsk": 1.2100,
              "LowAsk": 1.1950,
              "Count": 5,
              "IsComplete": true
            },
            {
              "OpenAsk": 1.2050,
              "CloseAsk": 1.2100,
              "HighAsk": 1.2150,
              "LowAsk": 1.2000,
              "Count": 6,
              "IsComplete": false
            }
          ],
          "DataVersion": 2
        }
        """));

    ChartData result = chartClient.getChartData(220, "FxSpot", 5, 100);

    assertThat(result.Data()).hasSize(2);
  }

  @Test
  void getChartData_withDifferentAssetTypes() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [],
          "DataVersion": 1
        }
        """));

    chartClient.getChartData(1, "Stock", 1, 50);

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).contains("AssetType=Stock");
  }
}
