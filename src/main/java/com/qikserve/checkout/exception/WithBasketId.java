package com.qikserve.checkout.exception;

public interface WithBasketId extends WithMessage {
    public Long getBasketId();
}