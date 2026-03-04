package com.saxolab.openapi.model.ref;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstrumentList(
        List<Instrument> Data,
        int MaxRows
) {}
