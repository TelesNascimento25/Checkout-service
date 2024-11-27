package com.qikserve.checkout.service;

import com.qikserve.checkout.exception.BasketItemNotFoundException;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.model.dto.Product;
import com.qikserve.checkout.repository.BasketItemRepository;
import com.qikserve.checkout.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasketItemServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BasketItemRepository basketItemRepository;

    @InjectMocks
    private BasketService basketService;

    @InjectMocks
    private BasketItemService basketItemService;

    @Test
    public void getProductsById_WhenItemsHaveProducts_ThenReturnProducts() {
        // Given
        var product = new Product();
        var item = new BasketItem();

        var a = product.withId("A");
        var b = product.withId("B");
        var items = List.of(
                item.withProductId(a.getId()),
                item.withProductId(b.getId())
        );

        when(productRepository.findAllById(any())).thenReturn(List.of(a, b));

        // When
        var productsById = basketItemService.getProductsById(items);

        // Then
        assertThat(productsById)
            .hasSize(2)
            .containsAllEntriesOf(Map.of(
                a.getId(), a,
                b.getId(), b
            ));
    }

    @Test
    public void getBasketItem_WhenBasketItemExists_ThenReturnBasketItem() {
        // Given
        var id = 1L;
        var item = BasketItem.builder().id(id).build();
        when(basketItemRepository.findById(id)).thenReturn(Optional.of(item));

        // When
        var result = basketItemService.getBasketItem(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(item, result.get());
    }


    @Test
    public void getBasketItem_WhenBasketItemDoesNotExist_ThenReturnEmpty() {
        // Given
        var basketItemId = 1L;
        when(basketItemRepository.findById(basketItemId)).thenReturn(Optional.empty());

        // When
        Optional<BasketItem> result = basketItemService.getBasketItem(basketItemId);

        // Then
        assertTrue(result.isEmpty());
    }


    @Test
    void updateQuantityBasketItem_WhenBasketItemExists_ThenReturnUpdatedBasketItem() {
        // Given
        var id = 1L;
        var newQuantity = 5;
        var existing = BasketItem.builder().id(id).quantity(2).build();
        var updated = existing.withQuantity(newQuantity);
        when(basketItemRepository.findById(id)).thenReturn(Optional.of(existing));
        when(basketItemRepository.save(any(BasketItem.class))).thenAnswer(i -> i.getArgument(0));


        // When
        var result = basketItemService.update(id, updated);

        // Then
        assertEquals(newQuantity, result.getQuantity());

    }

    @Test
    void deleteBasketItem_WhenBasketItemExists_ThenDeleteBasketItem() {
        // Given
        var id = 1L;
        var existingBasketItem = BasketItem.builder().id(id).build();
        when(basketItemRepository.findById(id)).thenReturn(Optional.of(existingBasketItem));

        // When
        basketItemService.deleteBasketItem(id);

        // Then
        verify(basketItemRepository, times(1)).deleteById(id);
    }


    @Test
    void deleteBasketItem_WhenBasketItemDoesNotExist_ThenThrowException() {

        // Given
        var id = 1L;
        when(basketItemRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        var e = assertThrows(BasketItemNotFoundException.class, () -> basketItemService.deleteBasketItem(id));
        assertEquals(id, e.getBasketItemId());
    }

}
