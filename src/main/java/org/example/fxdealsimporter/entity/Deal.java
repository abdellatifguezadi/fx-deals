package org.example.fxdealsimporter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deal {
    
    @Id
    @Column(name = "deal_unique_id")
    private String dealUniqueId;
    
    @Column(name = "from_currency_iso_code", nullable = false, length = 3)
    private String fromCurrencyIsoCode;
    
    @Column(name = "to_currency_iso_code", nullable = false, length = 3)
    private String toCurrencyIsoCode;
    
    @Column(name = "deal_timestamp", nullable = false)
    private LocalDateTime dealTimestamp;
    
    @Column(name = "deal_amount", nullable = false, precision = 17, scale = 2)
    private BigDecimal dealAmount;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}