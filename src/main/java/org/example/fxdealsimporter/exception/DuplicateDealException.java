package org.example.fxdealsimporter.exception;

public class DuplicateDealException extends RuntimeException {
    public DuplicateDealException(String message) {
        super(message);
    }
}