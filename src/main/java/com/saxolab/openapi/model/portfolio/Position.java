package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Position(
    String PositionId,
    String AccountKey,
    Integer Uic,
    String AssetType,
    Double Amount,
    String Status,
    PositionBase PositionBase,
    PositionView PositionView) {}
