package org.example.fxdealsimporter.mapper;

import org.example.fxdealsimporter.dto.DealRequest;
import org.example.fxdealsimporter.dto.DealResponse;
import org.example.fxdealsimporter.entity.Deal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DealMapper {
    
    Deal toEntity(DealRequest request);
    
    DealResponse toResponse(Deal deal);
}