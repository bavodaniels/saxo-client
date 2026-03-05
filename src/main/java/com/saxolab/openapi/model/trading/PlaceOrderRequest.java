package com.saxolab.openapi.model.trading;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlaceOrderRequest(
    String AccountKey,
    Integer Uic,
    String AssetType,
    String BuySell,
    String OrderType,
    Double Amount,
    Double OrderPrice,
    String OrderDuration,
    String ManualOrder,
    String ExternalReference) {}
