    package com.qikserve.checkout.controller;

    import com.fasterxml.jackson.annotation.JsonView;
    import com.qikserve.checkout.model.Basket;
    import com.qikserve.checkout.model.BasketItem;
    import com.qikserve.checkout.model.dto.Savings;
    import com.qikserve.checkout.service.BasketService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import java.net.URI;

    @RestController
    @RequestMapping("/baskets")
    @RequiredArgsConstructor
    public class BasketController {

        private final BasketService basketService;

        @GetMapping("/{id}")
        @JsonView(Basket.Views.Read.class)
        public ResponseEntity<Basket> getBasket(@PathVariable("id") Long id){
            return ResponseEntity.of(basketService.getBasket(id));
        }

        @PostMapping
        @JsonView(Basket.Views.Created.class)
        public ResponseEntity<Basket> createBasket() {
            var basket = basketService.createBasket();
            return ResponseEntity.created(URI.create("/baskets/" + basket.getId().toString()))
                    .body(basket);
        }

        @PostMapping("/{id}/item")
        @JsonView(BasketItem.Views.Created.class)
        public ResponseEntity<BasketItem> addBasketItem(@PathVariable("id") Long id,
                                                        @RequestBody @JsonView(BasketItem.Views.Create.class) BasketItem basketItem){
            var item = basketService.addBasketItem(basketItem.withBasketId(id));
            return ResponseEntity.created(URI.create( "/basketItems/" + item.getId().toString()))
                    .body(item);
        }

        @PostMapping("/{id}/clear")
        public ResponseEntity<Void> clearBasket(@PathVariable("id") Long id){
            basketService.clearBasket(id);
            return ResponseEntity.noContent().build();
        }

        @PostMapping("/{id}/cancel")
        public ResponseEntity<Void> cancelBasket(@PathVariable("id") Long id){
            basketService.cancelBasket(id);
            return ResponseEntity.noContent().build();
        }

        @GetMapping("/{id}/savings")
        public ResponseEntity<Savings> getBasketSavings(@PathVariable("id") Long id){
            var savings = basketService.calculateSavings(id);
            return ResponseEntity.ok(savings);
        }

        @PostMapping("/{id}/checkout")
        @JsonView({Basket.Views.Read.class})
        public ResponseEntity<Basket> checkoutBasket(@PathVariable("id") Long id){
                Basket finishedBasket = basketService.checkout(id);
                return ResponseEntity.ok(finishedBasket);
        }

    }
