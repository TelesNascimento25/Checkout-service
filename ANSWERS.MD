# Checkout Service API - Design and Implementation

This document provides answers to follow-up questions and additional details regarding the design and implementation of the checkout service API.


# 1 Time Spent and Potential Additions:
I spent approximately three days on this test. Given additional time, I would prioritize enhancing security.  This would involve implementing an OAuth 2.0 authorization framework for secure resource access and potentially integrating a JSON Web Token (JWT) authentication system for stateless and secure user management.  This would provide a robust security layer, protecting the API from unauthorized access.  I would also explore adding more comprehensive logging and monitoring to track application performance and identify potential issues proactively.


# 2 Useful Feature:
One of the most impactful features in recent Java versions is the enhancement of pattern matching, specifically the introduction of primitive types in patterns, instanceof, and switch expressions (Preview). This simplifies code and improves readability when working with primitive types.  In this project, I leveraged this feature to determine the appropriate promotion strategy based on the promotion type:

`public static PromotionStrategy getPromotionStrategy(Promotion promotion) {
return switch (promotion.getType()) {
case BUY_X_GET_Y_FREE -> new BuyXGetYFreeStrategy((BuyXGetYFree) promotion);
case FLAT_PERCENT -> new FlatPercentStrategy((FlatPercent) promotion);
case QTY_BASED_PRICE_OVERRIDE -> new QtyBasedPriceOverrideStrategy((QtyBasedPriceOverride) promotion);
default -> null; // or throw an exception for an unknown promotion type
};
}`

This concise switch expression replaces a more verbose if-else chain, making the code cleaner and easier to maintain.  A core part of the promotion logic hinges on this efficient type handling.


# 3 Most Difficult Aspect:
The most challenging aspect was designing and implementing truly comprehensive integration tests that covered all possible scenarios and edge cases, especially considering the time constraints.  Thorough integration testing is crucial for ensuring the reliability and stability of the API, particularly its interaction with the WireMock stubs and the handling of various promotion strategies.


# 4 Production Issue Tracking:
While I attempted to integrate Grafana with Prometheus to monitor application metrics via Actuator endpoints, I encountered some difficulties getting this setup fully operational within the timeframe. As an alternative, I implemented detailed logging using Log4j 2. This allows for capturing crucial information about the application's behavior, including errors, warnings, and informational messages.  This log data can be invaluable for identifying and troubleshooting issues in a production environment.


# 5 Interpretation of Requirements and Deliverables:
I interpreted the API as a middleware layer between the frontend and backend, responsible for managing shopping cart functionality, including creating, modifying, and checking out baskets.  I aimed for a comprehensive implementation that addressed the core requirements of a typical e-commerce platform, similar to a food delivery service like IFood.

MUST Have: The Java API consuming the WireMock data and handling promotions was implemented, along with automated unit and integration tests.

SHOULD Have: Link to the java api hosted() (exceptions have internationalization implemented).

COULD Have: Multi-tenancy with different WireMock instances was implemented.  

I prioritized core functionality and a robust structure within the given timeframe.  While a graphical user interface was not explicitly required, I focused on building a fully functional API that could serve as a solid foundation for a complete application. The project includes entities, repositories, services, and controllers, following best practices for code organization and maintainability.