package com.saxolab.openapi.model.trading;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChangeOrderRequest(
        String AccountKey,
        int Uic,
        String AssetType,
        String OrderType,
        Double Amount,
        Double OrderPrice,
        String OrderDuration
) {}
