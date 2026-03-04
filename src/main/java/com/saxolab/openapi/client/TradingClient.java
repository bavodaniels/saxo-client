package com.saxolab.openapi.client;

import com.saxolab.openapi.model.trading.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

@HttpExchange("/trade/v2")
public interface TradingClient {

    @PostExchange("/orders")
    OrderResponse placeOrder(@RequestBody PlaceOrderRequest request);

    @PatchExchange("/orders/{orderId}")
    OrderResponse changeOrder(@PathVariable String orderId, @RequestBody ChangeOrderRequest request);

    @DeleteExchange("/orders/{orderId}")
    void cancelOrder(@PathVariable String orderId, @RequestParam String AccountKey);

    @GetExchange("/orders")
    OrderList getOrders(@RequestParam String AccountKey);

    @GetExchange("/orders/{orderId}")
    Order getOrder(@PathVariable String orderId, @RequestParam String AccountKey);
}
