package org.example.fxdealsimporter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fxdealsimporter.dto.BatchImportResponse;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;
import org.example.fxdealsimporter.service.IDealService;
import org.example.fxdealsimporter.service.Impl.DealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
@Slf4j
public class DealController {
    
    private final IDealService dealService;
    
    @PostMapping
    public ResponseEntity<DealResponse> importDeal(@Valid @RequestBody DealRequest dealRequest) {
        log.info("Received deal import request");
        DealResponse response = dealService.importDeal(dealRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/batch")
    public ResponseEntity<BatchImportResponse> importDeals(@RequestBody List<DealRequest> dealRequests) {
        log.info("Received batch deal import request for {} deals", dealRequests.size());
        BatchImportResponse response = dealService.importDeals(dealRequests);
        return ResponseEntity.ok(response);
    }
    

}