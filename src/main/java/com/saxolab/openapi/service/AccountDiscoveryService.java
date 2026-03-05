package com.saxolab.openapi.service;

import com.saxolab.openapi.client.PortfolioClient;
import com.saxolab.openapi.model.portfolio.Account;
import com.saxolab.openapi.model.portfolio.AccountList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountDiscoveryService {

  private static final Logger log = LoggerFactory.getLogger(AccountDiscoveryService.class);

  private final PortfolioClient portfolioClient;

  public AccountDiscoveryService(PortfolioClient portfolioClient) {
    this.portfolioClient = portfolioClient;
  }

  /**
   * Automatically discovers and returns the first active account key.
   *
   * @return the AccountKey of the first active account
   * @throws IllegalStateException if no active accounts are found
   */
  public String discoverAccountKey() {
    AccountList accounts = portfolioClient.getAccounts();

    if (accounts == null || accounts.Data() == null || accounts.Data().isEmpty()) {
      throw new IllegalStateException("No accounts found");
    }

    Optional<String> activeAccountKey =
        accounts.Data().stream().filter(Account::Active).map(Account::AccountKey).findFirst();

    if (activeAccountKey.isPresent()) {
      String accountKey = activeAccountKey.get();
      log.info("Discovered active account: {}", accountKey);
      return accountKey;
    }

    // If no active account, return the first account
    String firstAccountKey = accounts.Data().get(0).AccountKey();
    log.info("No active account found, using first account: {}", firstAccountKey);
    return firstAccountKey;
  }

  /**
   * Get all available accounts.
   *
   * @return list of all accounts
   */
  public List<Account> getAllAccounts() {
    AccountList accounts = portfolioClient.getAccounts();
    if (accounts == null || accounts.Data() == null) {
      return List.of();
    }
    return accounts.Data();
  }
}
