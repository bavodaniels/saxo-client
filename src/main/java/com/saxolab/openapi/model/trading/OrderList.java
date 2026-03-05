package com.saxolab.openapi.model.trading;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderList(List<Order> Data, Integer Count) {}
