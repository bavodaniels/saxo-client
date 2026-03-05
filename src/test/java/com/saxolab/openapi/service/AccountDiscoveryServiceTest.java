package com.saxolab.openapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.saxolab.openapi.client.PortfolioClient;
import com.saxolab.openapi.model.portfolio.Account;
import com.saxolab.openapi.model.portfolio.AccountList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountDiscoveryServiceTest {

  private PortfolioClient portfolioClient;
  private AccountDiscoveryService service;

  @BeforeEach
  void setUp() {
    portfolioClient = mock(PortfolioClient.class);
    service = new AccountDiscoveryService(portfolioClient);
  }

  @Test
  void discoverAccountKey_returnsActiveAccount() {
    Account activeAccount =
        new Account("ID-1", "ACTIVE-KEY-123", "CLIENT-1", "TYPE", "USD", true, "Active Account");
    AccountList accountList = new AccountList(List.of(activeAccount));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    String accountKey = service.discoverAccountKey();

    assertThat(accountKey).isEqualTo("ACTIVE-KEY-123");
  }

  @Test
  void discoverAccountKey_returnsFirstActiveAccountWhenMultipleExists() {
    Account account1 =
        new Account("ID-1", "INACTIVE-KEY-1", "CLIENT-1", "TYPE", "USD", false, "Inactive 1");
    Account account2 =
        new Account("ID-2", "ACTIVE-KEY-2", "CLIENT-2", "TYPE", "USD", true, "Active 2");
    Account account3 =
        new Account("ID-3", "ACTIVE-KEY-3", "CLIENT-3", "TYPE", "USD", true, "Active 3");
    AccountList accountList = new AccountList(List.of(account1, account2, account3));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    String accountKey = service.discoverAccountKey();

    assertThat(accountKey).isEqualTo("ACTIVE-KEY-2");
  }

  @Test
  void discoverAccountKey_returnsFirstAccountWhenNoActiveAccountFound() {
    Account account1 =
        new Account("ID-1", "INACTIVE-KEY-1", "CLIENT-1", "TYPE", "USD", false, "Inactive 1");
    Account account2 =
        new Account("ID-2", "INACTIVE-KEY-2", "CLIENT-2", "TYPE", "USD", false, "Inactive 2");
    AccountList accountList = new AccountList(List.of(account1, account2));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    String accountKey = service.discoverAccountKey();

    assertThat(accountKey).isEqualTo("INACTIVE-KEY-1");
  }

  @Test
  void discoverAccountKey_throwsWhenNoAccountsReturned() {
    AccountList accountList = new AccountList(List.of());

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    assertThatThrownBy(() -> service.discoverAccountKey())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No accounts found");
  }

  @Test
  void discoverAccountKey_throwsWhenAccountListDataIsNull() {
    AccountList accountList = new AccountList(null);

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    assertThatThrownBy(() -> service.discoverAccountKey())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No accounts found");
  }

  @Test
  void discoverAccountKey_throwsWhenAccountListIsNull() {
    when(portfolioClient.getAccounts()).thenReturn(null);

    assertThatThrownBy(() -> service.discoverAccountKey())
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No accounts found");
  }

  @Test
  void getAllAccounts_returnsAllAccounts() {
    Account account1 = new Account("ID-1", "KEY-1", "CLIENT-1", "TYPE", "USD", true, "Account 1");
    Account account2 = new Account("ID-2", "KEY-2", "CLIENT-2", "TYPE", "USD", false, "Account 2");
    Account account3 = new Account("ID-3", "KEY-3", "CLIENT-3", "TYPE", "USD", true, "Account 3");
    AccountList accountList = new AccountList(List.of(account1, account2, account3));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).hasSize(3).containsExactly(account1, account2, account3);
  }

  @Test
  void getAllAccounts_returnsEmptyListWhenNoAccounts() {
    AccountList accountList = new AccountList(List.of());

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).isEmpty();
  }

  @Test
  void getAllAccounts_returnsEmptyListWhenDataIsNull() {
    AccountList accountList = new AccountList(null);

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).isEmpty();
  }

  @Test
  void getAllAccounts_returnsEmptyListWhenAccountListIsNull() {
    when(portfolioClient.getAccounts()).thenReturn(null);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).isEmpty();
  }

  @Test
  void getAllAccounts_returnsSingleAccount() {
    Account account = new Account("ID-1", "SINGLE-KEY", "CLIENT-1", "TYPE", "USD", true, "Single");
    AccountList accountList = new AccountList(List.of(account));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).hasSize(1).containsExactly(account);
  }

  @Test
  void discoverAccountKey_findsFirstActiveAccountInLargeList() {
    List<Account> accounts = new java.util.ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      accounts.add(
          new Account(
              "ID-" + i,
              "KEY-" + i,
              "CLIENT-" + i,
              "TYPE",
              "USD",
              i % 3 != 0, // Some inactive
              "Account " + i));
    }
    AccountList accountList = new AccountList(accounts);

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    String accountKey = service.discoverAccountKey();

    assertThat(accountKey).isEqualTo("KEY-1");
  }

  @Test
  void getAllAccounts_returnsMultipleAccounts() {
    Account account1 = new Account("ID-1", "KEY-1", "CLIENT-1", "TYPE", "USD", true, "Acc 1");
    Account account2 = new Account("ID-2", "KEY-2", "CLIENT-2", "TYPE", "EUR", false, "Acc 2");
    Account account3 = new Account("ID-3", "KEY-3", "CLIENT-3", "TYPE", "GBP", true, "Acc 3");
    AccountList accountList = new AccountList(List.of(account1, account2, account3));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    List<Account> accounts = service.getAllAccounts();

    assertThat(accounts).hasSize(3).containsExactly(account1, account2, account3);
  }

  @Test
  void discoverAccountKey_withInactiveAccountReturnsFirst() {
    Account inactiveAccount =
        new Account("ID-1", "INACTIVE-KEY", "CLIENT-1", "TYPE", "USD", false, "Inactive");
    AccountList accountList = new AccountList(List.of(inactiveAccount));

    when(portfolioClient.getAccounts()).thenReturn(accountList);

    String accountKey = service.discoverAccountKey();

    assertThat(accountKey).isEqualTo("INACTIVE-KEY");
  }
}
