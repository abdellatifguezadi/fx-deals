package org.example.fxdealsimporter.service;

import org.example.fxdealsimporter.dto.BatchImportResponse;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;
import org.example.fxdealsimporter.entity.Deal;
import org.example.fxdealsimporter.exception.InvalidCurrencyException;
import org.example.fxdealsimporter.mapper.DealMapper;
import org.example.fxdealsimporter.repository.DealRepository;
import org.example.fxdealsimporter.service.Impl.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
    
    @Mock
    private DealRepository dealRepository;
    
    @Mock
    private DealMapper dealMapper;
    
    @Mock
    private ICurrencyValidationService currencyValidationService;
    
    @InjectMocks
    private DealService dealService;
    
    private DealRequest dealRequest;
    private Deal deal;
    
    @BeforeEach
    void setUp() {
        dealRequest = DealRequest.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .build();
        
        deal = Deal.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void importDeal_Success() {
        DealResponse expectedResponse = DealResponse.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .createdAt(LocalDateTime.now())
                .build();
        
        doNothing().when(currencyValidationService).validateCurrencies("USD", "EUR");
        when(dealRepository.existsByDealUniqueId("DEAL001")).thenReturn(false);
        when(dealMapper.toEntity(dealRequest)).thenReturn(deal);
        when(dealRepository.save(deal)).thenReturn(deal);
        when(dealMapper.toResponse(deal)).thenReturn(expectedResponse);
        
        DealResponse response = dealService.importDeal(dealRequest);
        
        assertNotNull(response);
        assertEquals("DEAL001", response.getDealUniqueId());
        assertEquals("USD", response.getFromCurrencyIsoCode());
        assertEquals("EUR", response.getToCurrencyIsoCode());
        assertEquals(new BigDecimal("1000.50"), response.getDealAmount());
        
        verify(currencyValidationService).validateCurrencies("USD", "EUR");
        verify(dealRepository).existsByDealUniqueId("DEAL001");
        verify(dealMapper).toEntity(dealRequest);
        verify(dealRepository).save(deal);
        verify(dealMapper).toResponse(deal);
    }
    
    @Test
    void importDeals_BatchWithOneInvalid() {
        DealRequest validRequest1 = DealRequest.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .build();
        
        DealRequest invalidRequest = DealRequest.builder()
                .dealUniqueId("DEAL002")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("USD")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("2000.00"))
                .build();
        
        DealRequest validRequest2 = DealRequest.builder()
                .dealUniqueId("DEAL003")
                .fromCurrencyIsoCode("GBP")
                .toCurrencyIsoCode("JPY")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("3000.00"))
                .build();
        
        List<DealRequest> requests = List.of(validRequest1, invalidRequest, validRequest2);
        
        doNothing().when(currencyValidationService).validateCurrencies("USD", "EUR");
        doThrow(new InvalidCurrencyException("From and to currencies cannot be the same: USD"))
            .when(currencyValidationService).validateCurrencies("USD", "USD");
        doNothing().when(currencyValidationService).validateCurrencies("GBP", "JPY");
        
        when(dealRepository.existsByDealUniqueId(anyString())).thenReturn(false);
        when(dealMapper.toEntity(any())).thenReturn(deal);
        when(dealRepository.save(any())).thenReturn(deal);
        when(dealMapper.toResponse(any())).thenReturn(DealResponse.builder().dealUniqueId("TEST").build());
        
        BatchImportResponse response = dealService.importDeals(requests);
        
        assertEquals(3, response.getTotalDeals());
        assertEquals(2, response.getSuccessfulDeals());
        assertEquals(1, response.getFailedDeals());
        assertEquals(2, response.getSuccessfulImports().size());
        assertEquals(1, response.getErrors().size());
        assertEquals("DEAL002", response.getErrors().get(0).getDealUniqueId());
        assertEquals("From and to currencies cannot be the same: USD", response.getErrors().get(0).getErrorMessage());
    }
    
    @Test
    void importDeal_InvalidCurrency_ShouldThrowException() {
        doThrow(new InvalidCurrencyException("From and to currencies cannot be the same: USD"))
            .when(currencyValidationService).validateCurrencies("USD", "USD");
        
        DealRequest sameCurrencyRequest = DealRequest.builder()
                .dealUniqueId("DEAL002")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("USD")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .build();
        
        InvalidCurrencyException exception = assertThrows(
            InvalidCurrencyException.class,
            () -> dealService.importDeal(sameCurrencyRequest)
        );
        
        assertEquals("From and to currencies cannot be the same: USD", exception.getMessage());
        verify(currencyValidationService).validateCurrencies("USD", "USD");
        verifyNoInteractions(dealRepository, dealMapper);
    }
}