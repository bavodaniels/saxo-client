package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PositionBase(
    String AccountKey,
    Double Amount,
    Integer Uic,
    String AssetType,
    Double OpenPrice,
    String Status) {}
