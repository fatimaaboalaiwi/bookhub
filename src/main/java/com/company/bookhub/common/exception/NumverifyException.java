package com.company.bookhub.common.exception;

public class NumverifyException extends RuntimeException {

    public NumverifyException(String message) {
        super(message);
    }

    public NumverifyException(String message, Throwable cause) {
        super(message, cause);
    }
}
