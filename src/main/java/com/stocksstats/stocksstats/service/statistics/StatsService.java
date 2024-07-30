package com.stocksstats.stocksstats.service.statistics;

import com.stocksstats.stocksstats.dto.MentionDateRequest;
import com.stocksstats.stocksstats.dto.MentionDateResponse;
import com.stocksstats.stocksstats.entity.Mention;
import com.stocksstats.stocksstats.entity.Stock;
import com.stocksstats.stocksstats.entity.StockMentionCount;
import com.stocksstats.stocksstats.repository.MentionRepo;
import com.stocksstats.stocksstats.repository.OriginRepo;
import com.stocksstats.stocksstats.repository.StockRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.stocksstats.stocksstats.utils.statistics.StatsMapper.toQueryMention;

@Service
public class StatsService {

    @Autowired
    private MentionRepo mentionRepo;

    @Autowired
    private OriginRepo originRepo;

    @Autowired
    private StockRepo stockRepo;

    public List<Stock> getStocks() {
        return stockRepo.findAll();
    }

    public MentionDateResponse getStocksAnalyzed(MentionDateRequest mentionDateRequest) {
        List<Mention> mentions = mentionRepo.findAllByLastMentionOrderByAmountAsc(mentionDateRequest.getDate());

        final var queryMentions = new ArrayList<MentionDateResponse.QueriedMention>();
        mentions.forEach(mentioned -> queryMentions.add(toQueryMention(mentioned)));

        return new MentionDateResponse(queryMentions);
    }

    public List<StockMentionCount> getStockMentionCountsByDate(LocalDate date) {
        return originRepo.countStockMentionsByDate(date);
    }

}
