package com.stocksstats.stocksstats.controller;

import com.stocksstats.stocksstats.dto.MentionDateRequest;
import com.stocksstats.stocksstats.dto.MentionDateResponse;

import com.stocksstats.stocksstats.entity.Stock;
import com.stocksstats.stocksstats.service.retrievestocks.RetrieveStocksMentionsService;
import com.stocksstats.stocksstats.service.statistics.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StocksController {

    @Autowired
    private RetrieveStocksMentionsService retrieveStocksMentionsService;

    @Autowired
    private StatsService statsService;

    @GetMapping("/analyze")
    public String analyzeSubreddit() {
        retrieveStocksMentionsService.analyzeSubreddit();
        return "Analysis completed";
    }

    @GetMapping("/stocks")
    public List<Stock> getStocks() {
        Logger.getLogger(StocksController.class.getName()).log(Level.INFO, "getStocks");
        return statsService.getStocks();
    }

    @PostMapping("/mentions-by-date")
    public MentionDateResponse getMentionsByDate(@RequestBody MentionDateRequest mentionDateRequest) {
        Logger.getLogger(StocksController.class.getName()).log(Level.INFO, mentionDateRequest.toString());
        return statsService.getStocksAnalyzed(mentionDateRequest);
    }

}