package com.qikserve.checkout.exception;

import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
@SuperBuilder
public class BasketItemNotFoundException extends BasketItemException {

    public static BasketItemNotFoundException of(Long basketItemId) {
        return BasketItemNotFoundException.builder()
                                     .httpStatus(HttpStatus.BAD_REQUEST)
                                     .messageCode("error.basketNotOpen")
                                     .basketItemId(basketItemId)
                                     .build();
    }
}