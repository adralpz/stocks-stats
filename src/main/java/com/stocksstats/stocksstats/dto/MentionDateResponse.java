package com.stocksstats.stocksstats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MentionDateResponse {

    private List<QueriedMention> mentions;

    @Data
    @Builder
    public static class QueriedMention {
        private String symbol;
        private Short amount;
        private String name;
        private Short reputation;
    }

}
