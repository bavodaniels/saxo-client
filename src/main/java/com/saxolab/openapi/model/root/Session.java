package com.saxolab.openapi.model.root;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Session(String TradeLevel, boolean IsMarketDataEnabled) {}
