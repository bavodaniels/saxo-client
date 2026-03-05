package com.saxolab.openapi.model.ref;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Instrument(
    int Uic,
    String AssetType,
    String Symbol,
    String Description,
    String CurrencyCode,
    String ExchangeId) {}
