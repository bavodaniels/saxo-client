package com.saxolab.openapi.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PositionList(List<Position> Data, Integer Count) {}
