
# Thoughts

Products:  products need an id, name, and price. Might add a description and image URL later if needed.  Initially, I'll mock this with Wiremock.

Shopping Baskets (Baskets):  Each basket needs an id, a status (ACTIVE, FINISHED, DELETED, CANCELED), a way to track the items inside, the total itemCount, the number of distinct productCount, and the final total price (but only shown when finished).

Basket Items: For each product in a basket, I'll need a basketItemId, the basketId it belongs to, the productId, the quantity, the original price, and a list of any promotions applied.

Promotions:  Each promotion has an id, a type (like DISCOUNT, BUY_ONE_GET_ONE), and a value.

Now, for the API endpoints...  I'll organize them by resource:

/products (ProductController):  This will just forward to Wiremock for now.

GET /products: Get all products.  Should probably add pagination later.

GET /products/{id}: Get a single product by ID.

/baskets (BasketController):

GET /baskets: Get a list of baskets.  I'll filter by status and use pagination.  For ACTIVE baskets, I'll return a simplified view.  For FINISHED baskets, I'll include the total.

POST /baskets: Create a new basket.  Maybe allow adding items right away.  Return the basket ID, at least.

GET /baskets/{id}: Get a specific basket.  Show the full details.

POST /baskets/{id}/clear: Empty a basket (remove all items).

POST /baskets/{id}/cancel: Cancel a basket.

GET /baskets/{id}/savings: Calculate potential savings for an ACTIVE basket.

POST /baskets/{id}/checkout:  Finalize the checkout process, apply promotions, and mark the basket as FINISHED.

/basketItems (BasketItemController):

GET /basketItems: Get basket items.  basketId is required.  Add pagination and sorting.

GET /basketItems/{id}: Get a single basket item.

PATCH /basketItems/{id}: Update the quantity of an item.

DELETE /basketItems/{id}: Delete an item.

POST /basketItems: Add a new item to a basket.  Only for ACTIVE baskets.

# Implementation

PostgreSQL:  Use PostgreSQL for the database.

Spring Data JPA: Repositories for database access.  Custom queries for aggregates like basket totals.

Strategy Pattern

Spring Validation: Validate request parameters.

Custom Exceptions:  Create custom exceptions for things like invalid quantities (BasketInvalidQuantityException).

Controller Advice: Global exception handling for consistent error responses.

Wiremock: Mock the product service initially.  Cache responses for speed.

Caching:  Cache other things as needed.

Logging and Metrics:  Add logging (Log4j2) and metrics later.

No Auth (for now):  Skip authentication for now.

First things first, I'll set up the database and migrations, then create the entities and repositories.  After that, I'll work on the services, controllers, and Wiremock integration.  Testing is important throughout!  Finally, I'll swap out Wiremock for the real product service and add caching and observability.

resources

baseURL: api-version

# ProductController

forward from wiremock

GET /products

GET /products/:id
# BasketController

GET /baskets/?status=ACTIVE&FINISHED
// reduced basket representation
-> items
-> total quantity?
// -> total // with promotions already applied?
-> status // only active
// paginated
## Response
[
{
"id": "123",
"itemCount": 2,
"items": [ "1", "2" ],
"productCount": 4,
// "total": 123.45,    // display only if finished
"status": "ACTIVE",
},
]

POST /baskets
{
"items": [
{
"productId": "1",
"quantity": 2
},
{
"productId": "1",   // may repeat products
"quantity": 1
},
{
"productId": "2",
"quantity": 20  // > 0!! may not be empty, custom exception, test that
}
]
}
// create new basket (empty)
// allow option to provide items
return id (at least)

## Response
// full basket representation
{
"id": "123",
"status": "ACTIVE",
"items": [ "1", "2", "3" ],
"itemCount": 3,
"productCount": 23
}

GET /baskets/:id
// full basket representation
if FINISHED, display total

[//]: # (DELETE /baskets/:id)

[//]: # (-- change internal status to DELETED)

POST /baskets/:id/clear
-- remove all items from basket, effectively deleting items from database
POST /baskets/:id/cancel
GET /baskets/:id/savings
// compute savings for basket, available only for ACTIVE baskets
{
"savings": 12.34
}
POST /baskets/:id/checkout // -> status: FINISHED == PAYED, here we can apply promotions

Basket
{
"id": "123",
"status": "ACTIVE" | "FINISHED" | "DELETED" | "CANCELED",
"items": [ "1", "2", "3" ], // ids
"itemCount": 3,
"productCount": 23,
"total": 123.45 // display only if finished
}

# BasketItemController

GET /basketItems/?basketId=123&page=0&size=10&sortBy=quantity&sortDirection=ASC
// paginated
// basketId query param is mandatory
[
{
"id": "1",
"basketId": "123",
"productId": "1",
"quantity": 2,
"price": 12.34,
"promotions": [
{
"id": "1",
"type": "DISCOUNT",
"value": 0.1
}
]
},
{
"id": "2",
"basketId": "123",
"productId": "1",
"quantity": 1,
"price": 12.34,
"promotions": []
},
{
"id": "3",
"basketId": "123",
"productId": "2",
"quantity": 20,
"price": 12.34,
"promotions": []
}
]

GET /basketItems/:id
// full basket item representation
{
"id": "1",
"basketId": "123",
"productId": "1",
"quantity": 2,
"price": 12.34,
"promotions": [
{
"id": "1",
"type": "DISCOUNT",
"value": 0.1
}
]
}

PATCH /basketItems/:id
{
"quantity": 3 // > 0
}

DELETE /basketItems/:id
// delete for real

POST /basketItems
// only for active baskets
{
"basketId": "123",
"productId": "1",
"quantity": 2 // > 0
}


// spring validation, same model different validation
// jackson, ignore null

migrations

webmvc tests

start with wiremock, change wiremock or change api provider, bean for provider

entities for
tables
enums

// auto generate ids, created and updated timestamps, set automatically, based on previous project

repositories, queries for aggregates
