package com.stocksstats.stocksstats.service.retrievestocks;

import com.stocksstats.stocksstats.dto.MentionDateRequest;
import com.stocksstats.stocksstats.dto.MentionDateResponse;
import com.stocksstats.stocksstats.entity.Mention;
import com.stocksstats.stocksstats.repository.MentionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.stocksstats.stocksstats.utils.retrievestocks.StatsMapper.toQueryMention;

@Service
public class StatsService {

    @Autowired
    private MentionRepo mentionRepo;

    public MentionDateResponse getStocksAnalyzed(MentionDateRequest mentionDateRequest) {
        List<Mention> mentions = mentionRepo.findAllByCreatedAtOrderByAmountAsc(mentionDateRequest.getDate());

        var queryMentions = new ArrayList<MentionDateResponse.QueriedMention>();
        mentions.forEach(mentioned -> {
            queryMentions.add(toQueryMention(mentioned));
        });

        return new MentionDateResponse(queryMentions);
    }

}
