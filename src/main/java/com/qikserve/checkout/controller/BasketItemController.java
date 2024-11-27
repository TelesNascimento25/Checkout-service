package com.qikserve.checkout.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.service.BasketItemService;
import com.qikserve.checkout.service.BasketService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/basketItems")
@RequiredArgsConstructor
public class BasketItemController {

    private final BasketItemService basketItemService;
    private final BasketService basketService;

    @GetMapping("/{id}")
    @JsonView(BasketItem.Views.Read.class)
    public ResponseEntity<BasketItem> getBasketItem(@PathVariable("id") Long id) {
        return ResponseEntity.of(basketItemService.getBasketItem(id));
    }

    @PatchMapping("/{id}")
    @JsonView(BasketItem.Views.Read.class)
    public ResponseEntity<BasketItem> update(@PathVariable("id") Long id,
                                             @RequestBody @JsonView(BasketItem.Views.Update.class) BasketItem item) {
        return ResponseEntity.ok(basketItemService.update(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBasketItem(@PathVariable("id") Long id){
        basketItemService.deleteBasketItem(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @JsonView(BasketItem.Views.Created.class)
    public ResponseEntity<BasketItem> createBasketItem(@RequestBody @JsonView(BasketItem.Views.Create.class) BasketItem item) {
        var basketItem = basketService.addBasketItem(item);
        return ResponseEntity.created(URI.create("/basketItems/" + basketItem.getId().toString()))
                .body(basketItem);
    }

}
