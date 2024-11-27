package com.qikserve.checkout.exception;

import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@SuperBuilder
public class BasketNotOpenException extends BasketException {

    public static BasketNotOpenException of(Long basketId) {
        return BasketNotOpenException.builder()
                                     .httpStatus(HttpStatus.BAD_REQUEST)
                                     .messageCode("error.basketNotOpen")
                                     .basketId(basketId)
                                     .build();
    }
}
