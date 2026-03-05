package com.saxolab.openapi.model.ref;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstrumentDetails(
    int Uic,
    String AssetType,
    String Symbol,
    String Description,
    String CurrencyCode,
    String ExchangeId,
    Double MinimumTradeSize,
    Double TickSize,
    int OrderDistanceMinimum) {}
