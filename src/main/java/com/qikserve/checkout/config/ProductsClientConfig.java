package com.qikserve.checkout.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Configuration
public class ProductsClientConfig {

    @Bean(name = "productsClient")
    @Profile({"wiremock-client", "test"})
    public WebClient wiremockClient() {
        var server = new WireMockServer(wireMockConfig()
                .dynamicPort()
                .withRootDirectory("src/main/resources/wiremock"));
        server.start();
        return WebClient.create(server.baseUrl());
    }

    @Bean(name = "productsClient")
    @Profile("api-client")
    public WebClient apiClient(
            @Value("${product.api.base.url}") String baseUrl,
            @Value("${product.api.token}") String token) {
        var builder = WebClient.builder()
                .baseUrl(baseUrl);
        if (Objects.nonNull(token)) {
            builder.defaultHeaders(
                    header -> header.setBearerAuth(token)
            );
        }
        return builder.build();
    }
}
