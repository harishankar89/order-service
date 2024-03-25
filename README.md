# Order Service

## Overview

The Order Service is a Spring Boot application designed to store and retrieve order details of a user.

## Technologies Used

- Spring Boot 3.2.4
- Java 17
- Caffeine Cache
- Jacoco for code coverage
- H2 Database
- Spring Data JPA
- OpenAPI (Swagger) for API documentation

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 17 or higher
- Maven 3+

### Installing

1. Unzip the project

2. Build the project
```bash
mvn clean install
```
3. Run the application
```bash
mvn spring-boot:run
```
4. Access OpenAPI documentation at http://localhost:8080/swagger-ui.html

5. Once built, we can get the coverage report from target/site/jacoco/index.html

### Running the tests
To run the tests, use the following command:
```bash
mvn clean test
```

## Solution Structure

- **Controller Layer** (`OrderController`): Exposes REST APIs for creating order for a user, and fetching orders details and calculate price for a period.
- **Service Layer**:
  - `OrderService`: Aggregate Items, save Order details in DB with discount, get all orders for user and calculate price for period .
  - `ItemService`: Calculate discount on Item unit price based on quantity.
- **Repository Layer** (`OrderRepository`): Responsible for saving and retrieving data from DB.
- **Exception Handling** (`GlobalExceptionHandler`): Centralized exception handling.
- **Configuration** (`CacheConfig`, `DiscountConfig`): Manages application properties and caching configuration.

## Assumptions and Design Approach for Order Service

This design aims to provide a robust and efficient solution for orders while ensuring maintainability, and ease of use.

1. **Data Source**:
  - Will use a different DB for deploying in environments instead of H2.

2. **Caching**:
  - A caching mechanism is implemented to for same request fast access.
  - The cache is updated when the createOrder is called.
  - In Real-world scenarios, its preferable to use a distributed cache like Redis or Memcached to ensure that the cache is available across multiple instances of the service.

3. **API Design**:
  - The service exposes RESTful APIs to perform saving and fetching of order details.
  - Error handling is implemented to manage exceptions and provide meaningful error messages to the clients.

4. **Logging and Monitoring**:
  - Logging is implemented to track the operations of the service and to debug issues.
  - Monitoring could be set up (not covered in this implementation) to track the performance and health of the service. But actuator is used as a starting point.

5. **Price Calculation**:
  - Price is calculated based on discount applied on the items.

6. **Discount Calculation**:
  - Discount is calculated based on the quantity of the item.

## Error Handling

Custom exception (`OrderServiceException`) and a global exception handler are used to manage exceptions in a centralized manner.

## Testing

Unit and integration tests have been written to ensure the correctness of the solution, using JUnit 5 and MockMvc for testing.

## Coverage

Jacoco is used for code coverage analysis, ensuring that the solution is well-tested.

## Things that can be enhanced

- Add User and Item master tables and validate for entry present as a validation.
- Add docker file to build a postgres DB and also containerize after package.
- Add service level and method level documentation. since the code is self-explanatory, I have not added it.
- Add pagination and sorting for fetching orders.
- Use Spring Web Flux for reactive programming.
- Add more test cases for better coverage.
- Add more validations for input data.
- Add more profiles for better deployment.
- Add more metrics for better analysis.
