package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountBalance(
    Double CashBalance,
    Double TotalValue,
    Double UnrealizedProfitLoss,
    Double MarginAvailable,
    Double MarginUsedByCurrentPositions,
    String Currency) {}
