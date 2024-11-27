package com.qikserve.checkout.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.assertj.core.util.Arrays;

@SuperBuilder
@Getter(onMethod_ = {@Override})
public abstract class BasketException extends BaseException implements WithBasketId {
    private final Long basketId;

    @Override
    protected Object[] getArgs() {
        return Arrays.array(basketId);
    }
}