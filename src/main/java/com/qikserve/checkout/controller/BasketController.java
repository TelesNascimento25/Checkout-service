    package com.qikserve.checkout.controller;

    import com.fasterxml.jackson.annotation.JsonView;
    import com.qikserve.checkout.model.Basket;
    import com.qikserve.checkout.model.BasketItem;
    import com.qikserve.checkout.model.dto.Savings;
    import com.qikserve.checkout.service.BasketService;
    import jakarta.servlet.http.HttpServletRequest;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.net.URI;

    @RestController
    @RequestMapping("/baskets")
    @RequiredArgsConstructor
    public class BasketController {

        private final BasketService basketService;

        @GetMapping("/{id}")
        public ResponseEntity<Basket> getBasket(@PathVariable("id") Long id){
            return ResponseEntity.of(basketService.getBasket(id));
        }

        @PostMapping
        public ResponseEntity<Basket> createBasket() {
            var basket = basketService.createBasket();
            return ResponseEntity.created(URI.create("/baskets/" + basket.getId().toString()))
                    .body(basket);
        }

        @PostMapping("/{id}/item")
        public ResponseEntity<BasketItem> addBasketItem(@PathVariable("id") Long id,
                                                        @RequestBody BasketItem basketItem){
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
        public ResponseEntity<Basket> checkoutBasket(@PathVariable("id") Long id){
                Basket finishedBasket = basketService.checkout(id);
                return ResponseEntity.ok(finishedBasket);
        }

    }
