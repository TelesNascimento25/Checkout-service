package com.qikserve.checkout.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.assertj.core.util.Arrays;

@SuperBuilder
@Getter(onMethod_ = {@Override})
public abstract class BasketItemException extends BaseException implements WithBasketItemId {
    private final Long basketItemId;

    @Override
    protected Object[] getArgs() {
        return Arrays.array(basketItemId);
    }
}