package com.stocksstats.stocksstats.service;

import org.springframework.stereotype.Service;

import com.stocksstats.stocksstats.repository.MentionRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentionService {
    private final MentionRepo mentionRepo;
}
