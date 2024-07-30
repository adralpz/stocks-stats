package com.stocksstats.stocksstats.repository;

import com.stocksstats.stocksstats.entity.Origin;
import com.stocksstats.stocksstats.entity.StockMentionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OriginRepo extends JpaRepository<Origin, Integer> {

    @Query("SELECT m.symbol.id as stockId, m.symbol.symbol as stockSymbol, COUNT(o) as count " +
            "FROM Origin o " +
            "JOIN o.mention m " +
            "WHERE o.date = :date " +
            "GROUP BY m.symbol.id, m.symbol.symbol " +
            "ORDER BY COUNT(o) DESC")
    List<StockMentionCount> countStockMentionsByDate(@Param("date") LocalDate date);

}
