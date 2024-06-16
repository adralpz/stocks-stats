package com.stocksstats.stocksstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stocksstats.stocksstats.dto.Stock;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface StockRepo extends JpaRepository<Stock, String> {
}
