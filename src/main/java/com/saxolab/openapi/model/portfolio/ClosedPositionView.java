package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClosedPositionView(
    Double ProfitLoss, Double ProfitLossInBaseCurrency, String Currency) {}
