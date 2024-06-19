package com.stocksstats.stocksstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stocksstats.stocksstats.entity.Stock;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepo extends JpaRepository<Stock, Integer> {
}
