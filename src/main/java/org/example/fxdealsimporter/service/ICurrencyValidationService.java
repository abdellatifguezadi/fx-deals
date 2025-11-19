package org.example.fxdealsimporter.service;

import org.example.fxdealsimporter.dto.DealRequest;

public interface ICurrencyValidationService {
    void validateCurrencies(String fromCurrency, String toCurrency);
    void validateDealRequest(DealRequest dealRequest);
}