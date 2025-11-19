package org.example.fxdealsimporter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchImportResponse {
    private int totalDeals;
    private int successfulDeals;
    private int failedDeals;
    private List<DealResponse> successfulImports;
    private List<DealError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DealError {
        private String dealUniqueId;
        private String errorMessage;
    }
}