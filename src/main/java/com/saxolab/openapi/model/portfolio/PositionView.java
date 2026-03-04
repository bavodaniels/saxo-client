package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PositionView(
        Double CurrentPrice,
        Double ProfitLossOnTrade,
        String ConversionRateCurrent
) {}
