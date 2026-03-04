# Contributing to Saxo OpenAPI Client

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## Getting Started

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/saxo-openapi-client.git
   cd saxo-openapi-client
   ```
3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/bavodaniels/saxo-openapi-client.git
   ```
4. Create a feature branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Prerequisites

- Java 21 or higher
- Gradle 8.0+
- Git

### Building

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

All contributions must maintain 100% test pass rate.

## Making Changes

### Code Style

- Follow Google's Java Style Guide
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Add comments for complex logic

### Git Commit Messages

- Use clear, descriptive commit messages
- Start with an imperative verb (e.g., "Add", "Fix", "Update", "Remove")
- Keep the first line under 70 characters
- Reference issues when relevant: "Fix #123"

Example:
```
Add support for portfolio balances endpoint

- Implement getBalance() in PortfolioClient
- Add AccountBalance model record
- Include unit tests for balance retrieval
- Update documentation with balance examples

Fixes #42
```

## Testing Requirements

### Unit Tests

- Add tests for all new features
- Maintain existing test coverage
- Use MockWebServer for HTTP mocking
- Test both success and error scenarios

Example test structure:
```java
@Test
void testNewFeature() {
    // Arrange
    mockServer.enqueue(new MockResponse().setBody(...));

    // Act
    var result = client.newMethod();

    // Assert
    assertThat(result).isEqualTo(expected);
    assertThat(mockServer.takeRequest().getPath()).isEqualTo("/path");
}
```

### Integration Testing

- Run `./gradlew build` to ensure full integration
- Verify rate-limit-client integration if modified
- Test with both SIM and Live configurations (in documentation)

## Submitting Changes

### Pull Request Process

1. Ensure all tests pass:
   ```bash
   ./gradlew test
   ```

2. Update documentation if needed:
   - README.md for user-facing changes
   - CHANGELOG.md for version history
   - Code comments for implementation details

3. Push to your fork:
   ```bash
   git push origin feature/your-feature-name
   ```

4. Create a Pull Request on GitHub with:
   - Clear title and description
   - Reference to related issues
   - Summary of changes made
   - Any breaking changes noted

### PR Requirements

- All tests pass
- Code follows style guidelines
- Commits are clean and descriptive
- Documentation is updated
- No merge conflicts with upstream/main

## Adding API Endpoints

When adding new Saxo API endpoints:

1. Create request/response model records in `src/main/java/com/saxolab/openapi/model/`
2. Add methods to appropriate client interface in `src/main/java/com/saxolab/openapi/client/`
3. Use `@HttpExchange` annotations with correct HTTP verb and path
4. Add comprehensive unit tests in `src/test/java/com/saxolab/openapi/`
5. Update README.md with usage examples

Example:
```java
@PostExchange("/orders")
OrderResponse placeOrder(@RequestBody PlaceOrderRequest request);
```

## Reporting Issues

When reporting bugs:

1. Check existing issues first (closed and open)
2. Provide:
   - Clear description of the problem
   - Steps to reproduce
   - Expected vs actual behavior
   - Java and Spring Boot versions
   - Relevant log output

## Questions or Discussion

- Open an issue for discussions
- Label discussions appropriately
- Be respectful and constructive

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Help others learn and grow
- Report inappropriate behavior

## License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing! 🎉
