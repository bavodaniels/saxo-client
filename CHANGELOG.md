# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-SNAPSHOT] - 2026-03-04

### Added

- Initial release of Saxo OpenAPI client library
- OAuth 2.0 Authorization Code flow with automatic token refresh
- Five declarative HTTP clients via Spring `@HttpExchange`:
  - `TradingClient` - Order management (place, change, cancel, get)
  - `PortfolioClient` - Account, position, and balance queries
  - `ReferenceDataClient` - Instrument search and details
  - `ChartClient` - Chart and candle data retrieval
  - `RootServicesClient` - Session and user information
- Automatic token refresh scheduling (configurable margin before expiry)
- Thread-safe in-memory token storage with pluggable interface
- Bearer token injection via `ClientHttpRequestInterceptor`
- Integration with `rate-limit-client` for transparent rate limiting
- Spring Boot auto-configuration with sensible defaults
- Support for both SIM and Live Saxo environments
- Comprehensive unit tests (21 tests, 100% pass rate)
  - OAuth token exchange and refresh validation
  - Bearer token header injection verification
  - Token refresh scheduling confirmation
  - Rate limit header tracking
  - JSON serialization/deserialization tests
- Complete documentation with quick-start guides and examples

### Technical Details

- **Java Version**: Java 21+
- **Spring Boot**: 3.3.5+
- **Spring Framework**: 6.1.x+
- **Build Tool**: Gradle with Kotlin DSL
- **Dependencies**: Minimal - only Spring Boot and Spring Web (rate-limit-client optional)

---

## Future Enhancements

- Additional API endpoints as Saxo OpenAPI expands
- Support for streaming data (WebSocket integration)
- Enhanced error handling with typed exceptions
- Request/response logging interceptor
- Circuit breaker pattern integration
- Metrics and monitoring support
