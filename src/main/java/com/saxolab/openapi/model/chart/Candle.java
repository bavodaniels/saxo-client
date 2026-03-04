package com.saxolab.openapi.model.chart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Candle(
        String Time,
        Double OpenBid,
        Double HighBid,
        Double LowBid,
        Double CloseBid,
        Double OpenAsk,
        Double HighAsk,
        Double LowAsk,
        Double CloseAsk,
        Double Volume
) {}
