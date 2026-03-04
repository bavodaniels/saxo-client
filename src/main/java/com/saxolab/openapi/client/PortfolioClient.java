package com.saxolab.openapi.client;

import com.saxolab.openapi.model.portfolio.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("/port/v1")
public interface PortfolioClient {

    @GetExchange("/accounts/me")
    AccountList getAccounts();

    @GetExchange("/accounts/{AccountKey}")
    Account getAccount(@PathVariable String AccountKey);

    @GetExchange("/balances")
    AccountBalance getBalance(@RequestParam String AccountKey);

    @GetExchange("/positions/me")
    PositionList getPositions();

    @GetExchange("/positions/{PositionId}")
    Position getPosition(@PathVariable String PositionId);

    @GetExchange("/positions")
    PositionList getPositionsByAccount(@RequestParam String AccountKey);
}
