package com.saxolab.openapi.client;

import com.saxolab.openapi.model.ref.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/ref/v1")
public interface ReferenceDataClient {

    @GetExchange("/instruments")
    InstrumentList searchInstruments(
            @RequestParam String Keywords,
            @RequestParam String AssetTypes);

    @GetExchange("/instruments/details/{Uic}/{AssetType}")
    InstrumentDetails getInstrumentDetails(
            @PathVariable int Uic,
            @PathVariable String AssetType);

    @GetExchange("/instruments/details")
    InstrumentDetailsList getInstrumentDetailsList(
            @RequestParam String Uics,
            @RequestParam String AssetTypes);
}
