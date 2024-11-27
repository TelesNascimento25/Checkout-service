package com.qikserve.checkout.service;

import com.qikserve.checkout.exception.BasketInvalidQuantityException;
import com.qikserve.checkout.exception.BasketNotFoundException;
import com.qikserve.checkout.exception.BasketNotOpenException;
import com.qikserve.checkout.model.Basket;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.model.BasketStatus;
import com.qikserve.checkout.model.dto.Savings;
import com.qikserve.checkout.repository.BasketItemRepository;
import com.qikserve.checkout.repository.BasketRepository;
import com.qikserve.checkout.util.PenceUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.hamcrest.Matchers;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.comparator.Comparators;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BasketService {
    private final BasketRepository basketRepository;
    private final BasketItemService basketItemService;
    private final BasketItemRepository basketItemRepository;

    public Optional<Basket> getBasket(Long id) {
        return basketRepository.findById(id);
    }

    public Basket createBasket() {
        var basket = Basket.builder()
                           .status(BasketStatus.OPEN)
                           .build();
        return basketRepository.save(basket);
    }

    public BasketItem addBasketItem(BasketItem basketItem) {
        var id = basketItem.getBasketId();
        basketItemService.validateQuantity(basketItem);
        this.validateOpenBasket(this.getBasketById(id, false));
        return basketItemRepository.save(BasketItem.builder()
                                                   .basketId(id)
                                                   .productId(basketItem.getProductId())
                                                   .quantity(basketItem.getQuantity())
                                                   .build());

    }

    public void cancelBasket(Long id) {
        var basket = this.getBasketById(id, false);
        BasketNotOpenException.of(id).throwIf(!BasketStatus.OPEN.equals(basket.getStatus()));
        basket.setStatus(BasketStatus.CANCELLED);
        basketRepository.save(basket);
    }

    public void clearBasket(Long id) {
        basketRepository.clearBasket(id);
    }

    public Savings calculateSavings(Long id) {
        var basketItems = this.getBasketById(id, true).getBasketItems();
        var totalPrice = PenceUtils.toPounds(basketItemService.computeTotalPrice(basketItems));
        var promotionalPrice = PenceUtils.toPounds(basketItemService.computePromotionalPrice(basketItems));
        var savings = totalPrice.subtract(promotionalPrice);

        return Savings.builder()
                      .totalPrice(totalPrice)
                      .promotionalPrice(promotionalPrice)
                      .savings(savings)
                      .build();
    }

    public Basket checkout(Long id) {
        var basket = this.getBasketById(id,true);
        this.validateOpenBasket(basket);
        basket.setTotal(PenceUtils.toPounds(basketItemService.computeTotalPrice(basket.getBasketItems())));
        basket.setStatus(BasketStatus.CHECKED_OUT);
        return basketRepository.save(basket);
    }


    private Basket getBasketById(Long id, boolean fetchItems) {
        var basket = fetchItems ? basketRepository.fetchCheckoutItemsById(id) : basketRepository.findById(id);
        return basket.orElseThrow(() -> BasketNotFoundException.of(id));
    }

    public void validateOpenBasket(Basket basket) {
        BasketNotOpenException.of(basket.getId()).throwIf(!BasketStatus.OPEN.equals(basket.getStatus()));
    }
}
