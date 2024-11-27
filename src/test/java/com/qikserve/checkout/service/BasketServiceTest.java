package com.qikserve.checkout.service;

import com.qikserve.checkout.exception.BasketNotOpenException;
import com.qikserve.checkout.model.Basket;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.model.BasketStatus;
import com.qikserve.checkout.repository.BasketItemRepository;
import com.qikserve.checkout.repository.BasketRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;


    @Mock
    private  BasketItemRepository basketItemRepository;

    @Spy
    private BasketItemService basketItemService = new BasketItemService( null, null);

    @InjectMocks
    private BasketService basketService;

    @Test
    public void getBasket_WhenBasketExists_ThenReturnBasket() {
        // Given
        var basketId = 1L;
        var basket = Basket.builder().id(basketId).status(BasketStatus.OPEN).build();
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        // When
        Optional<Basket> result = basketService.getBasket(basketId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(basket, result.get());
    }

    @Test
    public void getBasket_WhenBasketDoesNotExist_ThenReturnEmpty() {
        // Given
        var basketId = 1L;
        when(basketRepository.findById(basketId)).thenReturn(Optional.empty());

        // When
        Optional<Basket> result = basketService.getBasket(basketId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    public void createBasket_ThenReturnNewBasket() {
        // Given
        var basket = Basket.builder().status(BasketStatus.OPEN).build();
        when(basketRepository.save(any(Basket.class))).thenAnswer(i -> i.getArgument(0, Basket.class).withId(1L));

        // When
        var result = basketService.createBasket();

        // Then
        assertNotNull(result);
        assertEquals(BasketStatus.OPEN, result.getStatus());
        assertEquals(1L, result.getId());
    }


    @Test
    public void addBasketItem_WhenBasketIsOpen_ThenSaveBasketItem() {
        // Given
        var id = 1L;
        var basketItem = BasketItem.builder().basketId(id).productId("1").quantity(1).build();
        var basket = Basket.builder().id(id).status(BasketStatus.OPEN).build();

        when(basketRepository.findById(id)).thenReturn(Optional.of(basket));
        when(basketItemRepository.save(any(BasketItem.class))).thenReturn(basketItem);
        // When
        var result = basketService.addBasketItem(basketItem);

        // Then
        assertEquals(basketItem, result);
    }

    @Test
    public void addBasketItem_WhenBasketIsNotOpen_ThenThrowException() {
        // Given
        var id = 1L;
        var basketItem = BasketItem.builder().basketId(id).productId("1").quantity(1).build();
        var basket = Basket.builder().id(id).status(BasketStatus.CANCELLED).build();

        when(basketRepository.findById(id)).thenReturn(Optional.of(basket));

        // When & Then
        var e = assertThrows(BasketNotOpenException.class, () -> basketService.addBasketItem(basketItem));
        assertEquals(id, e.getBasketId());
    }

    @Test
    public void cancelBasket_WhenBasketExists_ThenUpdateStatus() {
        // Given
        var basketId = 1L;
        var basket = Basket.builder().id(basketId).status(BasketStatus.OPEN).build();
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(basketRepository.save(any())).thenReturn(basket);

        // When
        basketService.cancelBasket(basketId);

        // Then
        assertEquals(BasketStatus.CANCELLED, basket.getStatus());
        verify(basketRepository, times(1)).save(basket);
    }


    @Test
    public void clearBasket_WhenBasketExists_ThenClearBasketItems() {
        // Given
        var basketId = 1L;

        // When
        basketService.clearBasket(basketId);

        // Then
        verify(basketRepository, times(1)).clearBasket(basketId);
    }

    @Test
    void checkout_WhenBasketNotOpen_ThenThrowBasketNotOpenException() {
        // Given
        var id = 1L;
        var basket = Basket.builder().id(id).status(BasketStatus.CANCELLED).build();
        when(basketRepository.fetchCheckoutItemsById(id)).thenReturn(Optional.of(basket));

        // When & Then
        assertThrows(BasketNotOpenException.class, () -> basketService.checkout(id));
    }

}

