package com.ptit.etax.common.error;

public class ConflictException extends RuntimeException {
    public ConflictException(String err) {
        super(err);
    }
}
