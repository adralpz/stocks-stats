package com.stocksstats.stocksstats.dto;

import com.stocksstats.stocksstats.entity.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockAnalyzed {

    private Stock stock;
    private Short amount;
    private List<DetectionOrigin> origin;

    @Data
    @AllArgsConstructor
    public static class DetectionOrigin {
        private String url;
        private String text;
    }

}