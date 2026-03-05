package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.ref.InstrumentDetailsList;
import com.saxolab.openapi.model.ref.InstrumentList;
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

class ReferenceDataClientTest {

  private MockWebServer mockWebServer;
  private ReferenceDataClient referenceDataClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    String baseUrl = mockWebServer.url("/").toString();
    RestClient restClient = TestRestClientFactory.createConfiguredRestClient(baseUrl);

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    referenceDataClient = factory.createClient(ReferenceDataClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void searchInstruments_sendsCorrectQueryParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    referenceDataClient.searchInstruments("EUR", "FxSpot");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).contains("/ref/v1/instruments");
    assertThat(request.getPath()).contains("Keywords=EUR");
    assertThat(request.getPath()).contains("AssetTypes=FxSpot");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void searchInstruments_deserializesEmptyResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    InstrumentList result = referenceDataClient.searchInstruments("XYZ", "Unknown");

    assertThat(result.Data()).isEmpty();
  }

  @Test
  void searchInstruments_deserializesResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "Uic": 100001,
              "AssetType": "FxSpot"
            }
          ]
        }
        """));

    InstrumentList result = referenceDataClient.searchInstruments("EUR", "FxSpot");

    assertThat(result.Data()).hasSize(1);
  }

  @Test
  void getInstrumentDetails_buildsCorrectPath() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Uic": 220,
          "AssetType": "FxSpot"
        }
        """));

    referenceDataClient.getInstrumentDetails(220, "FxSpot");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).isEqualTo("/ref/v1/instruments/details/220/FxSpot");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void getInstrumentDetailsList_sendsCorrectQueryParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    referenceDataClient.getInstrumentDetailsList("220,221", "FxSpot,Stock");

    RecordedRequest request = mockWebServer.takeRequest();
    assertThat(request.getPath()).contains("/ref/v1/instruments/details");
    assertThat(request.getPath()).contains("Uics=220%2C221");
    assertThat(request.getPath()).contains("AssetTypes=FxSpot%2CStock");
    assertThat(request.getMethod()).isEqualTo("GET");
  }

  @Test
  void getInstrumentDetailsList_deserializesEmptyResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    InstrumentDetailsList result = referenceDataClient.getInstrumentDetailsList("", "");

    assertThat(result.Data()).isEmpty();
  }

  @Test
  void getInstrumentDetailsList_deserializesMultipleResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "Uic": 220,
              "AssetType": "FxSpot"
            },
            {
              "Uic": 1,
              "AssetType": "Stock"
            }
          ]
        }
        """));

    InstrumentDetailsList result =
        referenceDataClient.getInstrumentDetailsList("220,1", "FxSpot,Stock");

    assertThat(result.Data()).hasSize(2);
  }

  @Test
  void searchInstruments_withMultipleResults() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "Uic": 220,
              "AssetType": "FxSpot"
            },
            {
              "Uic": 221,
              "AssetType": "FxSpot"
            }
          ]
        }
        """));

    InstrumentList result = referenceDataClient.searchInstruments("EUR", "FxSpot");

    assertThat(result.Data()).hasSize(2);
  }
}
