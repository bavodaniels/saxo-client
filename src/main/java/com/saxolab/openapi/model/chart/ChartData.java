package com.saxolab.openapi.model.chart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChartData(List<Candle> Data, int DataVersion) {}
