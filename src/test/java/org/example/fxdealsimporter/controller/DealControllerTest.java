package org.example.fxdealsimporter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.fxdealsimporter.dto.BatchImportResponse;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;
import org.example.fxdealsimporter.service.Impl.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
@ContextConfiguration(classes = {DealController.class, DealControllerTest.TestConfig.class})
class DealControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DealService dealService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Configuration
    static class TestConfig {
        @Bean
        @Primary
        public DealService dealService() {
            return mock(DealService.class);
        }
    }
    
    private DealRequest dealRequest;
    private DealResponse dealResponse;
    
    @BeforeEach
    void setUp() {
        dealRequest = DealRequest.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .build();
        
        dealResponse = DealResponse.builder()
                .dealUniqueId("DEAL001")
                .fromCurrencyIsoCode("USD")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void importDeal_Success() throws Exception {
        when(dealService.importDeal(any(DealRequest.class))).thenReturn(dealResponse);
        
        mockMvc.perform(post("/api/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL001"))
                .andExpect(jsonPath("$.fromCurrencyIsoCode").value("USD"))
                .andExpect(jsonPath("$.toCurrencyIsoCode").value("EUR"));
    }
    
    @Test
    void importDeals_BatchSuccess() throws Exception {
        BatchImportResponse batchResponse = BatchImportResponse.builder()
                .totalDeals(2)
                .successfulDeals(2)
                .failedDeals(0)
                .successfulImports(List.of(dealResponse, dealResponse))
                .errors(List.of())
                .build();
        
        when(dealService.importDeals(any())).thenReturn(batchResponse);
        
        mockMvc.perform(post("/api/deals/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(dealRequest, dealRequest))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDeals").value(2))
                .andExpect(jsonPath("$.successfulDeals").value(2))
                .andExpect(jsonPath("$.failedDeals").value(0));
    }
    
    @Test
    void importDeal_ValidationError() throws Exception {
        dealRequest = DealRequest.builder()
                .dealUniqueId("TEST001")
                .fromCurrencyIsoCode("INVALID")
                .toCurrencyIsoCode("EUR")
                .dealTimestamp(LocalDateTime.now())
                .dealAmount(new BigDecimal("1000.50"))
                .build();
        
        mockMvc.perform(post("/api/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dealRequest)))
                .andExpect(status().isBadRequest());
    }

}