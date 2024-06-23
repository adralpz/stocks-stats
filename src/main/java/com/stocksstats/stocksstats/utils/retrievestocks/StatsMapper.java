package com.stocksstats.stocksstats.utils.retrievestocks;

import com.stocksstats.stocksstats.dto.MentionDateResponse;
import com.stocksstats.stocksstats.entity.Mention;
import org.springframework.stereotype.Component;

@Component
public final class StatsMapper {

    public static MentionDateResponse.QueriedMention toQueryMention(Mention mention) {
        return MentionDateResponse.QueriedMention.builder()
                .symbol(mention.getSymbol().getSymbol())
                .amount(mention.getAmount())
                .name(mention.getSymbol().getName())
                .reputation(mention.getSymbol().getReputation())
                .build();
    }

}

