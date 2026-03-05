package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClosedPosition(
    String PositionId,
    ClosedPositionBase ClosedPositionBase,
    ClosedPositionView ClosedPositionView) {}
