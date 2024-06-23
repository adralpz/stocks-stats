package com.stocksstats.stocksstats.controller;

import com.stocksstats.stocksstats.dto.MentionDateRequest;
import com.stocksstats.stocksstats.dto.MentionDateResponse;
import com.stocksstats.stocksstats.service.retrievestocks.RetrieveStocksMentionsService;
import com.stocksstats.stocksstats.service.retrievestocks.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StocksController {

    @Autowired
    private RetrieveStocksMentionsService retrieveStocksMentionsService;

    @Autowired
    private StatsService statsService;

    @GetMapping("/analyze")
    public String analyzeSubreddit() {
        retrieveStocksMentionsService.analyzeSubreddit();
        return "Analizado";
    }
    @PostMapping("/mentions-by-date")
    public MentionDateResponse getMentionsByDate(@RequestBody MentionDateRequest mentionDateRequest) {
        return statsService.getStocksAnalyzed(mentionDateRequest);
    }

}