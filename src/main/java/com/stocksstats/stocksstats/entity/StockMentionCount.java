package com.stocksstats.stocksstats.entity;

public interface StockMentionCount {
    Integer getStockId();
    String getStockSymbol();
    Long getCount();
}