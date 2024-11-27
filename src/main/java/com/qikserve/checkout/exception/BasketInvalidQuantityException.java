package com.qikserve.checkout.exception;

import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@SuperBuilder
public class BasketInvalidQuantityException extends BasketItemException {

    public static BasketInvalidQuantityException of(Long basketItemId) {
        return BasketInvalidQuantityException.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .messageCode("error.quantityGreaterThanZero")
                .basketItemId(basketItemId)
                .build();
    }

    public static BasketInvalidQuantityException of() {
        return of(null);
    }
}
