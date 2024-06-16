package com.stocksstats.stocksstats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockAnalyzed {

    private BigInteger id;
    private String stock;
    private Integer amount;
    private List<DetectionOrigin> origin;

    @Data
    @AllArgsConstructor
    public static class DetectionOrigin {
        private String url;
        private String text;
    }

}