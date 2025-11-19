package org.example.fxdealsimporter.service;

import org.example.fxdealsimporter.exception.InvalidCurrencyException;
import org.example.fxdealsimporter.service.Impl.CurrencyValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidationServiceTest {

    private CurrencyValidationService currencyValidationService;

    @BeforeEach
    void setUp() {
        currencyValidationService = new CurrencyValidationService();
    }

    @Test
    void validateCurrencies_ValidDifferentCurrencies_ShouldPass() {
        assertDoesNotThrow(() -> currencyValidationService.validateCurrencies("USD", "EUR"));
    }

    @Test
    void validateCurrencies_SameCurrencies_ShouldThrowException() {
        InvalidCurrencyException exception = assertThrows(
            InvalidCurrencyException.class,
            () -> currencyValidationService.validateCurrencies("USD", "USD")
        );
        assertEquals("From and to currencies cannot be the same: USD", exception.getMessage());
    }

    @Test
    void validateCurrencies_UnsupportedFromCurrency_ShouldThrowException() {
        InvalidCurrencyException exception = assertThrows(
            InvalidCurrencyException.class,
            () -> currencyValidationService.validateCurrencies("XYZ", "USD")
        );
        assertEquals("Unsupported from currency: XYZ", exception.getMessage());
    }

    @Test
    void validateCurrencies_UnsupportedToCurrency_ShouldThrowException() {
        InvalidCurrencyException exception = assertThrows(
            InvalidCurrencyException.class,
            () -> currencyValidationService.validateCurrencies("USD", "XYZ")
        );
        assertEquals("Unsupported to currency: XYZ", exception.getMessage());
    }

}