package com.qikserve.checkout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qikserve.checkout.config.LocaleConfiguration;
import com.qikserve.checkout.model.Basket;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.model.BasketStatus;
import com.qikserve.checkout.model.dto.Savings;
import com.qikserve.checkout.repository.BasketItemRepository;
import com.qikserve.checkout.repository.BasketRepository;
import com.qikserve.checkout.service.BasketItemService;
import com.qikserve.checkout.service.BasketService;
import io.vavr.CheckedFunction1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.ErrorResponse;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("wiremock-client")
public class BasketControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BasketRepository basketRepository;
    @MockitoBean
    private BasketItemService basketItemService;
    @MockitoBean
    private BasketItemRepository basketItemRepository;
    @MockitoSpyBean
    private BasketService basketService;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Function<Object, String> toStr = CheckedFunction1.of(mapper::writeValueAsString).unchecked();

    private static final String CONTEXT_PATH = "/baskets";

    @Test
    public void getBasketById_WhenBasketExists_ThenReturnBasket() throws Exception {
        // Given
        var basket = Basket.builder().id(Long.valueOf(1L)).status(BasketStatus.OPEN).build();

        // When
        when(basketRepository.findById(basket.getId())).thenReturn(Optional.of(basket));

        // Then
        this.webTestClient.get().uri(CONTEXT_PATH + "/" + basket.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class)
                .value(b -> Assertions.assertEquals(toStr.apply(basket), toStr.apply(b)));
    }

    @Test
    public void getBasketById_WhenBasketDoesNotExist_ThenReturnNotFound() throws Exception {
        // Given
        var basketId = 1L;

        // When
        when(basketRepository.findById(Long.valueOf(basketId))).thenReturn(Optional.empty());

        // Then
        this.webTestClient.get().uri(CONTEXT_PATH + "/" + basketId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    // get message properties from i18n for 2 locales
    @ParameterizedTest
    @ValueSource(strings = {"en", "es"})
    public void addBasketItem_WhenBasketIsNotOpen_ThenReturnBadRequest(String locale) throws Exception {
        // Given
        var basketId = 1L;
        var basketItem = BasketItem.builder().basketId(basketId).productId("1").quantity(1).build();
        var errorMessage = locale.equals("en") ? "The Basket with ID 1 is not open" : "El carrito con ID 1 no está abierto";

        // When
        when(basketRepository.findById(basketId)).thenReturn(Optional.of(Basket.builder().id(basketId).status(BasketStatus.CANCELLED).build()));

        // Then
        this.webTestClient.post().uri(CONTEXT_PATH + "/" + basketId + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Language", locale)
                .bodyValue(basketItem)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(s -> Assertions.assertEquals(errorMessage, s.getDetail()));
    }

    @Test
    public void createBasket_WhenCreatingBasket_ThenReturnCreated() throws Exception {
        // Given
        var basket = Basket.builder().id(1L).status(BasketStatus.OPEN).build();
        when(basketService.createBasket()).thenReturn(basket);

        // When
        var response = this.webTestClient.post().uri(CONTEXT_PATH)
                                         .accept(MediaType.APPLICATION_JSON)
                                         .exchange();

        // Then
        response.expectStatus().isCreated()
                .expectHeader().location(CONTEXT_PATH + "/1");
    }


//    @Test
//    public void addBasketItem_WhenAddingBasketItem_ThenReturnCreated() throws Exception {
//        // Given
//        var basketId = 1L;
//        var basketItem = BasketItem.builder().basketId(basketId).productId("1").quantity(1).build();
//        when(basketRepository.findById(basketId)).thenReturn(Optional.of(Basket.builder().id(basketId).status(BasketStatus.OPEN).build()));
//        when(basketService.addBasketItem(basketId, basketItem)).thenReturn(basketItem);
//
//        // When
//        var response = this.webTestClient.post().uri(CONTEXT_PATH + "/" + basketId + "/item")
//                                         .contentType(MediaType.APPLICATION_JSON)
//                                         .accept(MediaType.APPLICATION_JSON)
//                                         .bodyValue(basketItem)
//                                         .exchange();
//
//        // Then
//        response.expectStatus().isCreated()
//                .expectHeader().location("/basketItems/" + basketItem.getId());
//    }



    @Test
    public void clearBasket_WhenBasketExists_ThenReturnNoContent() throws Exception {
        // Given
        long basketId = 1L;

        // When & Then
        this.webTestClient.post()
                          .uri(CONTEXT_PATH + "/" + basketId + "/clear")
                          .exchange()
                          .expectStatus().isNoContent();
    }

//    @Test
//    public void cancelBasket_WhenBasketExists_ThenReturnNoContent() throws Exception {
//        // Given
//        long basketId = 1L;
//
//        // When & Then
//        this.webTestClient.post()
//                          .uri(CONTEXT_PATH + "/" + basketId + "/cancel")
//                          .exchange()
//                          .expectStatus().isNoContent();
//    }

//    @Test
//    public void getBasketSavings_WhenBasketExists_ThenReturnSavings() throws Exception {
//        // Given
//        long basketId = 1L;
//        var savings = Savings.builder()
//                             .totalPrice(BigDecimal.TEN)
//                             .promotionalPrice(BigDecimal.ONE)
//                             .savings(BigDecimal.valueOf(9))
//                             .build();
//        when(basketService.calculateSavings(basketId)).thenReturn(savings);
//
//        // When & Then
//        this.webTestClient
//                .get()
//                .uri(CONTEXT_PATH + "/" + basketId + "/savings")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(Savings.class)
//                .isEqualTo(savings);
//    }

//    @Test
//    public void checkoutBasket_WhenBasketExists_ThenReturnBasket() throws Exception {
//        // Given
//        long basketId = 1L;
//        var basket = Basket.builder().id(basketId).status(BasketStatus.CHECKED_OUT).total(BigDecimal.TEN).build();
//        when(basketService.checkout(basketId)).thenReturn(basket);
//
//
//        // When & Then
//        this.webTestClient.post()
//                          .uri(CONTEXT_PATH + "/" + basketId + "/checkout")
//                          .accept(MediaType.APPLICATION_JSON)
//                          .exchange()
//                          .expectStatus().isOk()
//                          .expectBody(Basket.class)
//                          .isEqualTo(basket);
//    }

}
