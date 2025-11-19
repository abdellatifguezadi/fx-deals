package org.example.fxdealsimporter.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fxdealsimporter.dto.BatchImportResponse;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;
import org.example.fxdealsimporter.entity.Deal;
import org.example.fxdealsimporter.exception.DuplicateDealException;
import org.example.fxdealsimporter.exception.InvalidDealException;
import org.example.fxdealsimporter.mapper.DealMapper;
import org.example.fxdealsimporter.repository.DealRepository;
import org.example.fxdealsimporter.service.ICurrencyValidationService;
import org.example.fxdealsimporter.service.IDealService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealService implements IDealService {
    
    private final DealRepository dealRepository;
    private final DealMapper dealMapper;
    private final ICurrencyValidationService currencyValidationService;
    
    @Override
    public DealResponse importDeal(DealRequest dealRequest) {
        if (dealRequest == null) {
            throw new InvalidDealException("Deal request cannot be null");
        }
        
        currencyValidationService.validateDealRequest(dealRequest);
        
        log.info("Importing deal with ID: {}", dealRequest.getDealUniqueId());
        
        currencyValidationService.validateCurrencies(
            dealRequest.getFromCurrencyIsoCode(), 
            dealRequest.getToCurrencyIsoCode()
        );
        
        if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
            log.warn("Duplicate deal detected with ID: {}", dealRequest.getDealUniqueId());
            throw new DuplicateDealException("Deal with ID " + dealRequest.getDealUniqueId() + " already exists");
        }
        
        Deal deal = dealMapper.toEntity(dealRequest);
        Deal savedDeal = dealRepository.save(deal);
        log.info("Deal imported successfully with ID: {}", savedDeal.getDealUniqueId());
        
        return dealMapper.toResponse(savedDeal);
    }
    

    
    @Override
    public BatchImportResponse importDeals(List<DealRequest> dealRequests) {
        log.info("Importing {} deals in batch", dealRequests.size());
        
        List<DealResponse> successfulImports = new ArrayList<>();
        List<BatchImportResponse.DealError> errors = new ArrayList<>();
        
        for (DealRequest dealRequest : dealRequests) {
            try {
                DealResponse response = importDeal(dealRequest);
                successfulImports.add(response);
            } catch (Exception e) {
                log.warn("Failed to import deal {}: {}", dealRequest.getDealUniqueId(), e.getMessage());
                errors.add(BatchImportResponse.DealError.builder()
                    .dealUniqueId(dealRequest.getDealUniqueId())
                    .errorMessage(e.getMessage())
                    .build());
            }
        }
        
        log.info("Batch import completed: {} successful, {} failed", successfulImports.size(), errors.size());
        
        return BatchImportResponse.builder()
            .totalDeals(dealRequests.size())
            .successfulDeals(successfulImports.size())
            .failedDeals(errors.size())
            .successfulImports(successfulImports)
            .errors(errors)
            .build();
    }
    

}