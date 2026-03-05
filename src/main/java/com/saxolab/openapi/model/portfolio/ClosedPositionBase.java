package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClosedPositionBase(
    Integer Uic,
    String AssetType,
    String BuySell,
    Double Amount,
    Double OpenPrice,
    Double ClosingPrice,
    String OpenDate,
    String CloseDate) {}
