package com.stocksstats.stocksstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stocksstats.stocksstats.entity.Origin;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginRepo extends JpaRepository<Origin, Integer> {
}
