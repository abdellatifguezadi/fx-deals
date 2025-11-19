package org.example.fxdealsimporter.exception;

public class InvalidDealException extends RuntimeException {
    public InvalidDealException(String message) {
        super(message);
    }
}