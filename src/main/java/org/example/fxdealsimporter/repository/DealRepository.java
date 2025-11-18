package org.example.fxdealsimporter.repository;

import org.example.fxdealsimporter.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deal, String> {
    boolean existsByDealUniqueId(String dealUniqueId);
}