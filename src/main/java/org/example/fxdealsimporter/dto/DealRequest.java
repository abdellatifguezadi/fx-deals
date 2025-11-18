package org.example.fxdealsimporter.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealRequest {
    
    @NotBlank(message = "Deal unique ID is required")
    private String dealUniqueId;
    
    @NotBlank(message = "From currency ISO code is required")
    @Size(min = 3, max = 3, message = "From currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From currency must be 3 uppercase letters")
    private String fromCurrencyIsoCode;
    
    @NotBlank(message = "To currency ISO code is required")
    @Size(min = 3, max = 3, message = "To currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To currency must be 3 uppercase letters")
    private String toCurrencyIsoCode;
    
    @NotNull(message = "Deal timestamp is required")
    private LocalDateTime dealTimestamp;
    
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Deal amount must be positive")
    @Digits(integer = 15, fraction = 2, message = "Deal amount format is invalid")
    private BigDecimal dealAmount;
}