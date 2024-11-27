package com.qikserve.checkout.exception;

import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import java.util.function.Predicate;

@Getter(onMethod_ = {@Override})
public abstract class BaseException extends RuntimeException implements WithMessage, WithResponseStatus {
    private final String messageCode;
    private final HttpStatus httpStatus;

    protected BaseException(BaseExceptionBuilder<?, ?> b) {
        this.messageCode = b.messageCode;
        this.httpStatus = b.httpStatus;
    }

    public final ErrorResponse toResponse(MessageSource messageSource) {
        return ErrorResponse.create(this, this.httpStatus, this.getMessage(messageSource));
    }

    public final String getMessage(MessageSource messageSource) {
        return messageSource.getMessage(messageCode, this.getArgs(), LocaleContextHolder.getLocale());
    }

    public final void throwIf(Predicate<BaseException> condition) {
        if (condition.test(this)) {
            throw this;
        }
    }

    public final void throwIf(boolean condition) {
        if (condition) {
            throw this;
        }
    }

    protected abstract Object[] getArgs();

    public static abstract class BaseExceptionBuilder<C extends BaseException, B extends BaseExceptionBuilder<C, B>> {
        private String messageCode;
        private HttpStatus httpStatus;

        public B messageCode(String messageCode) {
            this.messageCode = messageCode;
            return self();
        }

        public B httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "BaseException.BaseExceptionBuilder(super=" + super.toString() + ", messageCode=" + this.messageCode + ", httpStatus=" + this.httpStatus + ")";
        }

    }
}