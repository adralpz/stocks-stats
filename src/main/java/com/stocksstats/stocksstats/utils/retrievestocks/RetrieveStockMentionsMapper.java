package com.stocksstats.stocksstats.utils.retrievestocks;

import com.stocksstats.stocksstats.dto.StockAnalyzed;
import com.stocksstats.stocksstats.entity.Mention;
import com.stocksstats.stocksstats.entity.Origin;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public final class RetrieveStockMentionsMapper {

    public static Mention toMention(StockAnalyzed stockAnalyzed) {
        Mention mention = new Mention();
        mention.setSymbol(stockAnalyzed.getStock());
        mention.setAmount(stockAnalyzed.getAmount());
        mention.setCreatedAt(LocalDate.now());

        return mention;
    }

    public static Origin toOrigin(String url, String textFragment, Mention mention) {
        Origin origin = new Origin();
        origin.setUrl(url);
        origin.setTextFragment(textFragment);
        origin.setMention(mention);
        return origin;
    }



}
