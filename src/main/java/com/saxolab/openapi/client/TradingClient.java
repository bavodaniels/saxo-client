package com.saxolab.openapi.client;

import com.saxolab.openapi.model.trading.ChangeOrderRequest;
import com.saxolab.openapi.model.trading.Order;
import com.saxolab.openapi.model.trading.OrderList;
import com.saxolab.openapi.model.trading.OrderResponse;
import com.saxolab.openapi.model.trading.PlaceOrderRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/trade/v2")
public interface TradingClient {

  @PostExchange("/orders")
  OrderResponse placeOrder(@RequestBody PlaceOrderRequest request);

  @PatchExchange("/orders/{orderId}")
  OrderResponse changeOrder(@PathVariable String orderId, @RequestBody ChangeOrderRequest request);

  @DeleteExchange("/orders/{orderId}")
  void cancelOrder(
      @PathVariable String orderId, @RequestParam(name = "AccountKey") String accountKey);

  @GetExchange("/orders")
  OrderList getOrders(@RequestParam(name = "AccountKey") String accountKey);

  @GetExchange("/orders/{orderId}")
  Order getOrder(
      @PathVariable String orderId, @RequestParam(name = "AccountKey") String accountKey);
}
