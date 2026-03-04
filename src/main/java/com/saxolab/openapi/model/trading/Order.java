package com.saxolab.openapi.model.trading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Order(
        String OrderId,
        String AccountKey,
        int Uic,
        String AssetType,
        String BuySell,
        String OrderType,
        Double Amount,
        Double OrderPrice,
        String Status,
        String ExternalReference,
        String OpenOrderType,
        String Duration
) {}
