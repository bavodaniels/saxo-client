package com.saxolab.openapi.client;

import com.saxolab.openapi.model.portfolio.Account;
import com.saxolab.openapi.model.portfolio.AccountBalance;
import com.saxolab.openapi.model.portfolio.AccountList;
import com.saxolab.openapi.model.portfolio.Position;
import com.saxolab.openapi.model.portfolio.PositionList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/port/v1")
public interface PortfolioClient {

  @GetExchange("/accounts/me")
  AccountList getAccounts();

  @GetExchange("/accounts/{AccountKey}")
  Account getAccount(@PathVariable(name = "AccountKey") String accountKey);

  @GetExchange("/balances")
  AccountBalance getBalance(@RequestParam(name = "AccountKey") String accountKey);

  @GetExchange("/positions/me")
  PositionList getPositions();

  @GetExchange("/positions/{PositionId}")
  Position getPosition(@PathVariable(name = "PositionId") String positionId);

  @GetExchange("/positions")
  PositionList getPositionsByAccount(@RequestParam(name = "AccountKey") String accountKey);
}
