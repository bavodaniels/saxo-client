package com.saxolab.openapi.model.ref;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Instrument(
    Integer Uic,
    String AssetType,
    String Symbol,
    String Description,
    String CurrencyCode,
    String ExchangeId) {}
