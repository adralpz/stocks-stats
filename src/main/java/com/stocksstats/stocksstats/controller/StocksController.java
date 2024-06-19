package com.stocksstats.stocksstats.controller;

import com.stocksstats.stocksstats.service.RetrieveStocksMentionsService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StocksController {

    @Autowired
    private RetrieveStocksMentionsService retrieveStocksMentionsService;

    @GetMapping("/analyze")
    public String analyzeSubreddit() {
        retrieveStocksMentionsService.analyzeSubreddit();
        return "Analizado";
    }

}