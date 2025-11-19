package org.example.fxdealsimporter.service;

import org.example.fxdealsimporter.dto.BatchImportResponse;
import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;

import java.util.List;

public interface IDealService {
    DealResponse importDeal(DealRequest dealRequest);
    BatchImportResponse importDeals(List<DealRequest> dealRequests);
}