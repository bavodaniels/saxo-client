# Saxo OpenAPI Client

A lightweight, declarative Java Spring Boot client library for the [Saxo OpenAPI](https://www.developer.saxo/openapi) with built-in OAuth 2.0 authentication, automatic token refresh, and rate limiting support.

## Features

- **Declarative HTTP Clients**: Spring's `@HttpExchange` interface mechanism for clean, type-safe API consumption
- **OAuth 2.0 Support**: Authorization Code flow with automatic token refresh (configurable SIM/Live environments)
- **Automatic Token Management**: Scheduled token refresh ~60s before expiry, thread-safe storage
- **Rate Limiting**: Integrated with `rate-limit-client` library for transparent rate limit handling
- **Spring Boot Auto-Configuration**: Zero-boilerplate setup via `@ConfigurationProperties`
- **Minimal Dependencies**: Relies on Spring Boot and Spring Web only (plus optional rate-limit-client)
- **Test Coverage**: 21 unit tests covering OAuth flows, token refresh, interceptors, and serialization

## Quick Start

### 1. Add Dependency

```gradle
dependencies {
    implementation("com.bavodaniels:saxo-openapi-client:1.0.0-SNAPSHOT")
}
```

### 2. Configure

In `application.yml`:

```yaml
saxo:
  base-url: https://gateway.saxobank.com/sim/openapi      # SIM environment
  auth-base-url: https://sim.logonvalidation.net
  app-key: YOUR_APP_KEY                                   # OAuth client_id
  app-secret: YOUR_APP_SECRET                             # OAuth client_secret
  redirect-uri: http://localhost:8080/callback            # OAuth redirect URI
  timeout-seconds: 30
```

For **Live** environment, update:
```yaml
saxo:
  base-url: https://gateway.saxobank.com/openapi
  auth-base-url: https://live.logonvalidation.net
```

### 3. Use the Clients

```java
@RestController
public class TradingController {
    private final TradingClient tradingClient;
    private final SaxoOAuthService oauthService;

    public TradingController(TradingClient tradingClient, SaxoOAuthService oauthService) {
        this.tradingClient = tradingClient;
        this.oauthService = oauthService;
    }

    @GetMapping("/orders")
    public OrderList getOrders() {
        return tradingClient.getOrders();
    }

    @PostMapping("/trade")
    public OrderResponse placeOrder(@RequestBody PlaceOrderRequest request) {
        return tradingClient.placeOrder(request);
    }

    @GetMapping("/auth/authorize")
    public String authorize() {
        String state = UUID.randomUUID().toString();
        return oauthService.buildAuthorizeUrl(state);
    }

    @GetMapping("/auth/callback")
    public String handleCallback(@RequestParam String code) {
        oauthService.exchangeCode(code);
        return "Authorization successful!";
    }
}
```

## API Clients

### 5 Built-in Client Interfaces

| Client | Prefix | Methods |
|--------|--------|---------|
| **TradingClient** | `/trade/v1` | `placeOrder()`, `changeOrder()`, `cancelOrder()`, `getOrders()`, `getOrder()` |
| **PortfolioClient** | `/port/v1` | `getAccounts()`, `getAccount()`, `getBalance()`, `getPositions()`, `getPosition()` |
| **ReferenceDataClient** | `/ref/v1` | `searchInstruments()`, `getInstrumentDetails()`, `getInstrumentDetailsList()` |
| **ChartClient** | `/chart/v1` | `getChartData()` |
| **RootServicesClient** | `/root/v1` | `getSessionCapabilities()`, `getUser()`, `changeSessionCapabilities()` |

All clients are auto-wired by Spring and ready to inject into your components.

## OAuth 2.0 Flow

### 1. Build Authorization URL

```java
String state = UUID.randomUUID().toString();
String authUrl = oauthService.buildAuthorizeUrl(state);
// Redirect user to: https://sim.logonvalidation.net/authorize?response_type=code&client_id=...&state=...
```

### 2. Exchange Authorization Code

```java
@GetMapping("/callback")
public void handleCallback(@RequestParam String code) {
    oauthService.exchangeCode(code);
    // Token is automatically stored and scheduled for refresh
}
```

### 3. Automatic Token Refresh

The library automatically refreshes tokens when they approach expiry. No manual action needed — tokens are injected into every API request via the `SaxoAuthInterceptor`.

## Configuration Properties

```yaml
saxo:
  base-url: https://gateway.saxobank.com/sim/openapi  # API base URL
  auth-base-url: https://sim.logonvalidation.net      # OAuth token/authorize endpoint
  app-key: YOUR_APP_KEY                                # OAuth client_id
  app-secret: YOUR_APP_SECRET                          # OAuth client_secret
  redirect-uri: http://localhost:8080/callback         # OAuth redirect URI
  timeout-seconds: 30                                  # HTTP request timeout
  token-refresh-margin-seconds: 60                     # Refresh token 60s before expiry
```

## Advanced Configuration

### Custom Token Storage

Implement `SaxoOAuthTokenStore` to persist tokens (e.g., database, Redis):

```java
@Configuration
public class CustomTokenStorageConfig {
    @Bean
    public SaxoOAuthTokenStore customTokenStore() {
        return new RedisTokenStore();  // your implementation
    }
}
```

The library will automatically use your custom store instead of the default in-memory store.

### Rate Limiting

If `rate-limit-client` is on the classpath, rate limiting is automatically applied to all Saxo API calls:

```gradle
dependencies {
    implementation("com.bavodaniels:rate-limit-client:1.0.0-SNAPSHOT")
}
```

Configure rate limits:

```yaml
rate-limit:
  enabled: true
  max-wait-time-millis: 10000
```

## Project Structure

```
src/main/java/com/saxolab/openapi/
├── config/
│   ├── SaxoAutoConfiguration.java      # Spring auto-config
│   └── SaxoProperties.java             # @ConfigurationProperties
├── auth/
│   ├── SaxoTokenProvider.java          # Token access interface
│   ├── SaxoOAuthService.java           # OAuth flow & token refresh
│   ├── SaxoOAuthClient.java            # OAuth HTTP client
│   ├── SaxoOAuthTokenStore.java        # Token storage interface
│   ├── InMemoryTokenStore.java         # Default implementation
│   ├── OAuthTokenResponse.java         # Token response record
│   └── SaxoAuthInterceptor.java        # Request interceptor
├── client/
│   ├── TradingClient.java
│   ├── PortfolioClient.java
│   ├── ReferenceDataClient.java
│   ├── ChartClient.java
│   └── RootServicesClient.java
└── model/
    ├── trading/                        # Order, OrderResponse, etc.
    ├── portfolio/                      # Account, Position, etc.
    ├── ref/                            # Instrument, etc.
    ├── chart/                          # ChartData, Candle, etc.
    └── root/                           # Session, User, etc.
```

## Building

```bash
./gradlew build
```

Runs compilation, tests (21 unit tests), and package verification.

## Testing

```bash
./gradlew test
```

Runs all 21 unit tests covering:
- OAuth token exchange and refresh
- Bearer token header injection
- Token refresh scheduling
- Rate limit integration
- JSON serialization/deserialization

## Requirements

- **Java 21+**
- **Spring Boot 3.3.x**
- **Spring Framework 6.1.x+**

## Dependencies

Core:
- `org.springframework.boot:spring-boot-starter`
- `org.springframework:spring-web`

Optional:
- `com.bavodaniels:rate-limit-client:1.0.0-SNAPSHOT` (for rate limiting)

## License

MIT License - see LICENSE file for details.

## Contributing

Contributions are welcome! Please ensure all tests pass and new features include test coverage.

## Resources

- [Saxo OpenAPI Docs](https://www.developer.saxo/openapi)
- [Spring RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
- [Spring @HttpExchange](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)
- [Spring Boot Auto-Configuration](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.developing-auto-configuration)
