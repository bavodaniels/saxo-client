package com.saxolab.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.saxolab.openapi.model.trading.ChangeOrderRequest;
import com.saxolab.openapi.model.trading.Order;
import com.saxolab.openapi.model.trading.OrderList;
import com.saxolab.openapi.model.trading.OrderResponse;
import com.saxolab.openapi.model.trading.PlaceOrderRequest;
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

class TradingClientTest {

  private MockWebServer mockWebServer;
  private TradingClient tradingClient;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();

    RestClient restClient = TestRestClientFactory.createConfiguredRestClient(mockWebServer.url("/").toString());

    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

    tradingClient = factory.createClient(TradingClient.class);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void placeOrder_sendsPostRequest() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "OrderId": "order-123",
          "Status": "Placed"
        }
        """));

    PlaceOrderRequest request =
        new PlaceOrderRequest(
            "account-key",
            220,
            "FxSpot",
            "Buy",
            "Market",
            1000000.0,
            1.1000,
            "DayOrder",
            null,
            null);
    OrderResponse result = tradingClient.placeOrder(request);

    assertThat(result).isNotNull();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath()).isEqualTo("/trade/v2/orders");
    assertThat(recordedRequest.getMethod()).isEqualTo("POST");
  }

  @Test
  void changeOrder_sendsPatchRequest() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "OrderId": "order-123",
          "Status": "Updated"
        }
        """));

    ChangeOrderRequest changeRequest =
        new ChangeOrderRequest(
            "account-key", 220, "FxSpot", "Market", 1000000.0, 1.1100, "DayOrder");
    OrderResponse result = tradingClient.changeOrder("order-123", changeRequest);

    assertThat(result).isNotNull();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath()).isEqualTo("/trade/v2/orders/order-123");
    assertThat(recordedRequest.getMethod()).isEqualTo("PATCH");
  }

  @Test
  void cancelOrder_sendsDeleteRequest() throws Exception {
    mockWebServer.enqueue(new MockResponse().setResponseCode(204));

    tradingClient.cancelOrder("order-123", "account-key-1");

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath())
        .isEqualTo("/trade/v2/orders/order-123?AccountKey=account-key-1");
    assertThat(recordedRequest.getMethod()).isEqualTo("DELETE");
  }

  @Test
  void getOrders_sendsGetRequest() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": []
        }
        """));

    OrderList result = tradingClient.getOrders("account-key-1");

    assertThat(result.Data()).isEmpty();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath()).isEqualTo("/trade/v2/orders?AccountKey=account-key-1");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
  }

  @Test
  void getOrder_sendsGetRequest() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "OrderId": "order-123",
          "Status": "Placed"
        }
        """));

    Order result = tradingClient.getOrder("order-123", "account-key-1");

    assertThat(result).isNotNull();

    RecordedRequest recordedRequest = mockWebServer.takeRequest();
    assertThat(recordedRequest.getPath())
        .isEqualTo("/trade/v2/orders/order-123?AccountKey=account-key-1");
    assertThat(recordedRequest.getMethod()).isEqualTo("GET");
  }

  @Test
  void getOrders_deserializesMultipleOrders() {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "Data": [
            {
              "OrderId": "order-1",
              "Status": "Placed"
            },
            {
              "OrderId": "order-2",
              "Status": "Executed"
            }
          ]
        }
        """));

    OrderList result = tradingClient.getOrders("account-key-1");

    assertThat(result.Data()).hasSize(2);
  }

  @Test
  void placeOrder_withVariousOrderTypes() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "OrderId": "limit-order-1",
          "Status": "Placed"
        }
        """));

    PlaceOrderRequest limitOrder =
        new PlaceOrderRequest(
            "account-key",
            220,
            "FxSpot",
            "Buy",
            "Limit",
            1000000.0,
            1.0950,
            "DayOrder",
            null,
            null);
    OrderResponse result = tradingClient.placeOrder(limitOrder);

    assertThat(result).isNotNull();
  }

  @Test
  void changeOrder_withDifferentParameters() throws Exception {
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody(
                """
        {
          "OrderId": "order-456",
          "Status": "Updated"
        }
        """));

    ChangeOrderRequest changeOrder =
        new ChangeOrderRequest("account-key", 100001, "Stock", "Market", 500.0, null, "DayOrder");
    OrderResponse result = tradingClient.changeOrder("order-456", changeOrder);

    assertThat(result).isNotNull();
  }
}
