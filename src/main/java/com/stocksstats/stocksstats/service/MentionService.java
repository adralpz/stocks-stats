package com.stocksstats.stocksstats.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import com.stocksstats.stocksstats.entity.Mention;
import com.stocksstats.stocksstats.repository.MentionRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentionService {
    private final MentionRepo mentionRepo;

    public void saveMention(String symbol, Short amount, Timestamp date) {
        Mention mention = new Mention();
        mention.setSymbol(symbol);
        mention.setAmount(amount);
        mention.setDate(date);
        mentionRepo.save(mention);
    }
}
