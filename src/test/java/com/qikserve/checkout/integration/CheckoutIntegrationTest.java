package com.qikserve.checkout.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qikserve.checkout.exception.BaseException;
import com.qikserve.checkout.exception.BasketInvalidQuantityException;
import com.qikserve.checkout.exception.BasketNotOpenException;
import com.qikserve.checkout.model.Basket;
import com.qikserve.checkout.model.BasketItem;
import com.qikserve.checkout.model.BasketStatus;
import com.qikserve.checkout.model.dto.Savings;
import io.vavr.CheckedFunction1;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("wiremock-client")
@AutoConfigureJsonTesters
public class CheckoutIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper mapper;


    private static final String BASKETS_PATH = "/baskets";
    private static final String ITEMS_PATH = "/basketItems";

    private Function<Object, String> toStr;

    @BeforeAll
    public static void setupGlobal() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @BeforeEach
    public void setup() {
        toStr = CheckedFunction1.of(mapper::writeValueAsString).unchecked();
    }

    @Test
    void completeCheckoutFlowTest() {
        var basket = createBasket();
        var id = basket.getId();

        var itemBuilder = BasketItem.builder().basketId(id);

        var item1 = itemBuilder.productId("PWWe3w1SDU").build();
        var item2 = itemBuilder.productId("Dwt5F7KAhi").build();

        addBasketItem(item1.withQuantity(4));
        addBasketItem(item2.withQuantity(2));
        simulateAndValidate(id, "61.94", "37.97", "23.97");

        addBasketItem(item1.withQuantity(1));
        simulateAndValidate(id, "71.93", "47.96", "23.97");

        addBasketItem(item2.withQuantity(0), BasketInvalidQuantityException::of);
        addBasketItem(item2.withQuantity(-10), BasketInvalidQuantityException::of);
        addBasketItem(item2.withQuantity(null), BasketInvalidQuantityException::of);

        clearBasket(id);
        simulateAndValidate(id, "0.00", "0.00", "0.00");

        var added1 = addBasketItem(item1.withQuantity(4));
        var added2 = addBasketItem(item2.withQuantity(2));
        var removed = addBasketItem(item1.withQuantity(100)).getId();

        removeBasketItem(removed);
        assertThat(getBasket(id).getItemsCount()).isEqualTo(2);

        simulateAndValidate(id, "61.94", "37.97", "23.97");

        var checkout = checkoutBasket(id);

        assertThat(checkout).isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(checkout.withId(id)
                        .withStatus(BasketStatus.CHECKED_OUT)
                        .withTotal(new BigDecimal("61.94")));
        assertThat(checkout.getBasketItems())
                .containsExactlyInAnyOrder(added1, added2);
        addBasketItem(item1.withQuantity(1), () -> BasketNotOpenException.of(id));
    }

    private Savings simulateAndValidate(Long basketId, String total, String promo, String saved) {
        var savings = getSavings(basketId);
        assertThat(getSavings(basketId)).satisfies(s -> {
            assertThat(s.getTotalPrice()).isEqualByComparingTo(total);
            assertThat(s.getPromotionalPrice()).isEqualByComparingTo(promo);
            assertThat(s.getSavings()).isEqualByComparingTo(saved);
        });
        return savings;
    }

    private Basket createBasket() {
        return this.webTestClient.post().uri(BASKETS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Basket.class).returnResult().getResponseBody();
    }

    private Void clearBasket(Long basketId) {
        return this.webTestClient.post().uri(BASKETS_PATH + "/" + basketId + "/clear")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty().getResponseBody();
    }

    private BasketItem addBasketItem(BasketItem item) {
        return this.webTestClient.post().uri(BASKETS_PATH + "/" + item.getBasketId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(toStr.apply(item))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BasketItem.class).returnResult().getResponseBody();
    }

    private ProblemDetail addBasketItem(BasketItem item, Supplier<BaseException> ex) {
        var response = ex.get().toResponse(messageSource);
        var body = response.getBody();
        return this.webTestClient.post().uri(BASKETS_PATH + "/" + item.getBasketId() + "/item")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, Locale.getDefault().getDisplayLanguage())
                .bodyValue(toStr.apply(item))
                .exchange()
                .expectStatus().isEqualTo(response.getStatusCode())
                .expectBody(ProblemDetail.class)
                .value(ProblemDetail::getDetail, equalTo(body.getDetail()))
                .value(ProblemDetail::getTitle, equalTo(body.getTitle()))
                .value(ProblemDetail::getStatus, equalTo(body.getStatus()))
                .value(ProblemDetail::getType, equalTo(body.getType()))
                .returnResult().getResponseBody();
    }


    private Basket checkoutBasket(Long basketId) {
        return this.webTestClient.post().uri(BASKETS_PATH + "/" + basketId + "/checkout")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class).returnResult()
                .getResponseBody();

    }

    private Void removeBasketItem(Long id) {
        return this.webTestClient.delete().uri(ITEMS_PATH + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty()
                .getResponseBody();
    }

    private Basket getBasket(Long basketId) {
        return this.webTestClient.get().uri(BASKETS_PATH + "/" + basketId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Basket.class).returnResult().getResponseBody();
    }

    private Savings getSavings(Long basketId) {
        return this.webTestClient.get().uri(BASKETS_PATH + "/" + basketId + "/savings")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Savings.class).returnResult().getResponseBody();
    }

}
