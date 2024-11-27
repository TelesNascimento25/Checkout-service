package com.qikserve.checkout.exception;

import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@SuperBuilder
public class BasketNotFoundException extends BasketException {

    public static BasketNotFoundException of(long basketId) {
        return BasketNotFoundException.builder()
                                      .httpStatus(HttpStatus.NOT_FOUND)
                                      .messageCode("error.basketNotFound")
                                      .basketId(basketId)
                                      .build();
    }
}