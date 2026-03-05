package com.saxolab.openapi.client;

import com.saxolab.openapi.model.ref.InstrumentDetails;
import com.saxolab.openapi.model.ref.InstrumentDetailsList;
import com.saxolab.openapi.model.ref.InstrumentList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/ref/v1")
public interface ReferenceDataClient {

  @GetExchange("/instruments")
  InstrumentList searchInstruments(
      @RequestParam(name = "Keywords") String keywords,
      @RequestParam(name = "AssetTypes") String assetTypes);

  @GetExchange("/instruments/details/{Uic}/{AssetType}")
  InstrumentDetails getInstrumentDetails(
      @PathVariable(name = "Uic") int uic, @PathVariable(name = "AssetType") String assetType);

  @GetExchange("/instruments/details")
  InstrumentDetailsList getInstrumentDetailsList(
      @RequestParam(name = "Uics") String uics,
      @RequestParam(name = "AssetTypes") String assetTypes);
}
