# Customer Management Service - Testing

## Overview

This project focuses on the implementation of robust and comprehensive testing for a REST API-based Customer Management Service. The testing framework incorporates tools like JUnit, Mockito, Testcontainers, and Jacoco to ensure reliability and maintainability.

## Testing Tools and Frameworks
- **JUnit 5:** For writing and running unit and integration tests.
- **Mockito:** Used to mock dependencies and isolate the units being tested.
- **Testcontainers:** Enables integration testing with a PostgreSQL database in a containerized environment.
- **Jacoco:** Provides code coverage reporting to ensure the adequacy of test coverage.

## Key Features of the Testing Suite

1. **Unit Tests:**

- Ensure the correctness of service and controller logic.
Dependencies are mocked using Mockito to isolate the unit under test.

**Integration Tests:**

- Verify end-to-end functionality of the REST API with a real database (PostgreSQL) using Testcontainers.
Test scenarios include CRUD operations and edge cases.

3. **Code Coverage:**

- Jacoco generates detailed coverage reports, ensuring all critical code paths are tested.

## Running the Tests

### Prerequisites:

- Install Java 17+ and Maven 3.6+.
- Ensure Docker is installed and running for Testcontainers.

#### Run All Tests:

```
mvn test
```
Or you can manually test all the tests for the particular test file in your IDE.

#### View Code Coverage:

After running tests, open the generated Jacoco report in the following path:

```target/site/jacoco/index.html```

Navigate to this file in your browser to view detailed coverage metrics.

#### Example Test Scenarios

1. **Unit Test**
- **CustomerServiceTest:**
Tests service-layer methods for creating, updating, and deleting customers.
Verifies exceptions like CustomerNotFoundException and CustomerEmailUnavailableException are properly thrown.

2. **Integration Test**
- **CustomerIntegrationTest**:
Uses Testcontainers to spin up a PostgreSQL container for testing the application in a realistic environment.
Covers scenarios like creating a customer, retrieving by ID, updating, and deleting.

## Contribution
Feel free to contribute to this project by adding more tests or enhancing existing ones. Open a pull request for any updates.

