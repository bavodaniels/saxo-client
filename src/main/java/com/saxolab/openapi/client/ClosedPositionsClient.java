package com.saxolab.openapi.client;

import com.saxolab.openapi.model.portfolio.ClosedPosition;
import com.saxolab.openapi.model.portfolio.ClosedPositionList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/port/v1")
public interface ClosedPositionsClient {

  @GetExchange("/closedpositions")
  ClosedPositionList getClosedPositions(
      @RequestParam(name = "AccountKey") String accountKey,
      @RequestParam(name = "FromDate") String fromDate,
      @RequestParam(name = "ToDate") String toDate,
      @RequestParam(name = "$top", required = false) Integer top,
      @RequestParam(name = "$skip", required = false) Integer skip);

  @GetExchange("/closedpositions/{PositionId}")
  ClosedPosition getClosedPosition(@PathVariable(name = "PositionId") String positionId);
}
