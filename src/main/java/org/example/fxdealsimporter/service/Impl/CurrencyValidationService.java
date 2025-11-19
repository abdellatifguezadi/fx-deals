package org.example.fxdealsimporter.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.exception.InvalidCurrencyException;
import org.example.fxdealsimporter.exception.InvalidDealException;
import org.example.fxdealsimporter.service.ICurrencyValidationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@Slf4j
public class CurrencyValidationService implements ICurrencyValidationService {
    
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
        "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD",
        "SEK", "NOK", "DKK", "PLN", "CZK", "HUF", "RUB", "CNY",
        "INR", "BRL", "MXN", "ZAR", "KRW", "SGD", "HKD", "THB"
    );

    @Override
    public void validateCurrencies(String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            throw new InvalidCurrencyException("From and to currencies cannot be the same: " + fromCurrency);
        }
        
        if (!SUPPORTED_CURRENCIES.contains(fromCurrency)) {
            throw new InvalidCurrencyException("Unsupported from currency: " + fromCurrency);
        }
        
        if (!SUPPORTED_CURRENCIES.contains(toCurrency)) {
            throw new InvalidCurrencyException("Unsupported to currency: " + toCurrency);
        }
        
        log.debug("Currency validation passed for {} to {}", fromCurrency, toCurrency);
    }
    
    @Override
    public void validateDealRequest(DealRequest dealRequest) {
        if (dealRequest.getDealUniqueId() == null || dealRequest.getDealUniqueId().trim().isEmpty()) {
            throw new InvalidDealException("Deal unique ID is required");
        }
        if (dealRequest.getFromCurrencyIsoCode() == null || !dealRequest.getFromCurrencyIsoCode().matches("^[A-Z]{3}$")) {
            throw new InvalidDealException("From currency must be 3 uppercase letters");
        }
        if (dealRequest.getToCurrencyIsoCode() == null || !dealRequest.getToCurrencyIsoCode().matches("^[A-Z]{3}$")) {
            throw new InvalidDealException("To currency must be 3 uppercase letters");
        }
        if (dealRequest.getDealTimestamp() == null) {
            throw new InvalidDealException("Deal timestamp is required");
        }
        if (dealRequest.getDealAmount() == null || dealRequest.getDealAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDealException("Deal amount must be positive");
        }
    }
}