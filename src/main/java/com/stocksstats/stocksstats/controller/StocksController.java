package com.stocksstats.stocksstats.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.stocksstats.stocksstats.service.RetrieveStocksMentionsService;

@RestController
@RequestMapping("/api")
public class StocksController {

    @Autowired
    private RetrieveStocksMentionsService retrieveStocksMentionsService;

    @GetMapping("/analyze")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public HashMap<String, Object> analyzeSubreddit() {
        // retrieveStocksMentionsService.analyzeSubreddit();
        HashMap<String, Object> response = new HashMap<>();
        response.put("message", "Hello World!");
        return response;
    }

    @GetMapping("/stocks")
    public List<String> getStocks() {
        List<String> stocks = RetrieveStocksMentionsService.getStocks();
        return stocks;
    }
}
