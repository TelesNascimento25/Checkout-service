package com.qikserve.checkout.controller;

import com.qikserve.checkout.model.dto.BuyXGetYFree;
import com.qikserve.checkout.model.dto.FlatPercent;
import com.qikserve.checkout.model.dto.Product;
import com.qikserve.checkout.model.dto.QtyBasedPriceOverride;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("wiremock-client")
public class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String CONTEXT_PATH = "/products";

    private static final Set<Product> products = Set.of(
            Product.builder().id("Dwt5F7KAhi").name("Amazing Pizza!").price(1099)
                   .promotions(List.of(
                    QtyBasedPriceOverride.builder().id("ibt3EEYczW").requiredQty(Integer.valueOf(2)).price(Integer.valueOf(1799)).build())).build(),
            Product.builder().id("PWWe3w1SDU").name("Amazing Burger!").price(999)
                   .promotions(List.of(
                    BuyXGetYFree.builder().id("ZRAwbsO2qM").requiredQty(Integer.valueOf(2)).freeQty(Integer.valueOf(1)).build())).build(),
            Product.builder().id("C8GDyLrHJb").name("Amazing Salad!").price(499)
                   .promotions(List.of(
                    FlatPercent.builder().id("Gm1piPn7Fg").amount(Integer.valueOf(10)).build())).build(),
            Product.builder().id("4MB7UfpTQs").name("Boring Fries!").price(199).build()
    );

    private static final Set<Product> productsReduced = products.stream()
            .map(p -> p.withPromotions((List.of())))
            .collect(Collectors.toSet());

    @Test
    public void getAllProducts() throws Exception {
        this.webTestClient.get().uri(CONTEXT_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .value(p -> Assertions.assertEquals(productsReduced, Set.copyOf(p)));
    }

    @ParameterizedTest
    @FieldSource("products")
    public void getProductById(Product product) throws Exception {
        this.webTestClient.get().uri(CONTEXT_PATH + "/" + product.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Product.class)
                .value(p -> Assertions.assertEquals(product, p));
    }

}
