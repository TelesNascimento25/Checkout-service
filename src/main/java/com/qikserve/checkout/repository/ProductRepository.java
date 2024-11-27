package com.qikserve.checkout.repository;

import com.qikserve.checkout.model.dto.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final WebClient productsClient;

    private static final String CONTEXT_PATH = "/products";

    public Optional<Product> findById(String productId) {
        return this.findByIdReactive(productId).blockOptional();
    }

    public List<Product> findAllById(Collection<String> productIds) {
        return this.findAllByIdReactive(productIds).collectList().block();
    }

    public List<Product> findAll() {
        return this.findAllReactive().collectList().block();
    }

    public Mono<Product> findByIdReactive(String productId) {
        return productsClient.get()
                .uri(CONTEXT_PATH + "/" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> switch (response.statusCode()) {
                    case OK -> response.bodyToMono(Product.class);
                    case NOT_FOUND -> Mono.empty();
                    default -> response.createError();
                });
    }

    public Flux<Product> findAllByIdReactive(Collection<String> productIds) {
        return this.findAllByIdReactive(Set.copyOf(productIds));
    }

    public Flux<Product> findAllByIdReactive(Set<String> productIds) {
        return Flux.fromIterable(productIds)
                .flatMap(this::findByIdReactive);
    }

    public Flux<Product> findAllReactive() {
        return productsClient.get()
                .uri(CONTEXT_PATH)
                .retrieve()
                .bodyToFlux(Product.class);
    }
}
