# Checkout Service API - Design and Implementation

This document describes the design and implementation of a checkout service API.  The goal is to provide a robust and flexible system for managing shopping baskets, applying promotions, and handling the checkout process.

## I. Core Concepts and Data Model

The system revolves around four key entities:

* **Product:** Represents a product available for purchase.  Each product has an `id`, `name`, and `price`.  Future enhancements could include adding a `description` and `imageUrl`.  Initially, the product data will be mocked using Wiremock.

* **Basket:** Represents a shopping cart.  Key attributes include:
    * `id`: Unique identifier for the basket.
    * `status`:  Indicates the basket's current state (ACTIVE, FINISHED, DELETED, CANCELED).
    * `items`: A collection of `BasketItem` objects representing the products in the basket.
    * `itemCount`: The total number of items in the basket.
    * `productCount`: The number of distinct products in the basket.
    * `total`: The final calculated price of the basket, including promotions (only displayed when `status` is FINISHED).

* **BasketItem:**  Represents a specific product within a basket. Attributes include:
    * `id`: Unique identifier for the basket item.
    * `basketId`:  The ID of the basket to which the item belongs.
    * `productId`: The ID of the product.
    * `quantity`: The number of units of the product in the basket.
    * `price`: The individual price of the product.
    * `promotions`: A list of promotions applied to this basket item.

* **Promotion:** Represents a discount or offer applicable to basket items.  Attributes:
    * `id`: Unique identifier for the promotion.
    * `type`: The type of promotion (e.g., DISCOUNT, BUY_ONE_GET_ONE).
    * `value`:  The value of the promotion (e.g., percentage discount, amount off).

## II. API Endpoints

The API is organized around these resources:

* **Products (`/products`, `ProductController`):**  (Initially forwards requests to Wiremock.)
    * `GET /products`: Retrieves all available products.  Pagination will be added in a future iteration.
    * `GET /products/{id}`: Retrieves a specific product by its ID.

* **Baskets (`/baskets`, `BasketController`):**
    * `GET /baskets`: Retrieves a list of baskets, filtered by `status`.  Supports pagination.  Returns a simplified representation for ACTIVE baskets and a full representation for FINISHED baskets.
    * `POST /baskets`: Creates a new basket.  Optionally accepts an initial list of items.  Returns the newly created basket's ID.
    * `GET /baskets/{id}`: Retrieves a specific basket by ID.  Returns the full basket representation.
    * `POST /baskets/{id}/clear`: Removes all items from a basket.
    * `GET /baskets/{id}/savings`: Calculates and returns the total savings for an ACTIVE basket based on applied promotions.
    * `POST /baskets/{id}/checkout`: Completes the checkout process. Applies promotions, calculates the final total, and updates the basket status to FINISHED.

* **Basket Items (`/basketItems`, `BasketItemController`):**
    * `GET /basketItems`: Retrieves basket items. Requires a `basketId` parameter. Supports pagination and sorting.
    * `GET /basketItems/{id}`: Retrieves a specific basket item by ID.
    * `PATCH /basketItems/{id}`: Updates the `quantity` of a basket item.  Validates that the new quantity is greater than zero.
    * `DELETE /basketItems/{id}`: Deletes a basket item.
    * `POST /basketItems`: Adds a new item to an ACTIVE basket.  Requires `basketId`, `productId`, and `quantity`.  Validates that the quantity is greater than zero.

## III. Implementation Details

* **Database:** PostgreSQL is used as the database for storing product, basket, and basket item information.
* **Spring Data JPA:** Spring Data JPA repositories provide a convenient abstraction for database access.  Custom queries are implemented for calculating aggregate values like basket totals.
* **Services:**  Business logic is encapsulated in service classes (`ProductService`, `BasketService`, `BasketItemService`).
* **Promotion Strategy:** The Strategy pattern is used to handle different promotion types.  This allows for easy addition of new promotion types without modifying existing code.
* **Multi-tenancy with Wiremock:**  The Wiremock integration is designed with multi-tenancy in mind.  This allows the checkout service to interact with different POS (Point of Sale) systems, each potentially having a unique data format.  Different Wiremock instances can be configured, each tailored to a specific POS system.  This adaptability ensures that the checkout service can handle diverse data structures without requiring code changes.  Only the `Product` data needs adjustments for each Wiremock instance; the core checkout service logic remains dynamic and independent of the POS data format.
* **Validation:** Spring Validation is used to validate incoming request parameters.
* **Custom Exceptions:** Custom exceptions, such as `BasketInvalidQuantityException`, are used to handle specific error conditions and provide more informative error messages.
* **Controller Advice:** A `GlobalExceptionHandler` is implemented to handle exceptions globally and return consistent error responses to the client.
* **Wiremock:** Wiremock is used for mocking the external product service during initial development and testing.  Caching of Wiremock responses is implemented to improve performance.
* **Caching:** Caching mechanisms can be further implemented for frequently accessed data to enhance overall API responsiveness.
* **Logging and Metrics:**  Log4j2 is used for logging.  Metrics will be added later for monitoring and performance analysis.
* **Authentication and Authorization:**  Currently, no authentication or authorization is implemented.

## IV. Development Process

The development process follows these key steps:

1. **Database Setup and Migrations:** Create the PostgreSQL database and implement database migrations using Flyway.
2. **Entity and Repository Implementation:** Define the entities (`Product`, `Basket`, `BasketItem`, `Promotion`) and create corresponding Spring Data JPA repositories.
3. **Service Implementation:** Implement the business logic in service classes.  This includes basket management, item manipulation, promotion application, and checkout processing.
4. **Controller Implementation:** Implement the REST controllers, handling request mapping, validation, and exception handling.
5. **Wiremock Integration and Testing:** Integrate with Wiremock for mocking the product service during development. Implement comprehensive unit and integration tests.
6. **Multi-Tenancy:** The Wiremock integration supports multi-tenancy.
7. **Product Service Integration:** Replace the Wiremock integration with the actual product service integration once it becomes available.
8. **Caching and Performance Optimization:** Implement caching strategies to improve API performance.
9. **Observability (Logging):** Configure logging for monitoring and troubleshooting.

## V. API Documentation with Swagger

The Swagger UI documentation can be accessed at the following URL:

[localhost:8082/swagger-ui/index.html#/](localhost:8082/swagger-ui/index.html#/).


## 6. JAVA API HOSTED Swagger
Link to the Swagger - Api hosted
[http://34.67.12.225/swagger-ui/index.html#](http://34.67.12.225/swagger-ui/index.html#).
