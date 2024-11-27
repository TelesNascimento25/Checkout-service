package com.qikserve.checkout.exception;

import org.springframework.http.HttpStatus;

public interface WithResponseStatus {
    public HttpStatus getHttpStatus();
}