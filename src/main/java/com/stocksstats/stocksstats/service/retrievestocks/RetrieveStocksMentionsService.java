package com.stocksstats.stocksstats.service.retrievestocks;

import com.stocksstats.stocksstats.config.Initializer;
import com.stocksstats.stocksstats.dto.StockAnalyzed;
import com.stocksstats.stocksstats.entity.Origin;
import com.stocksstats.stocksstats.entity.Stock;
import com.stocksstats.stocksstats.repository.MentionRepo;
import com.stocksstats.stocksstats.repository.OriginRepo;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.stocksstats.stocksstats.utils.retrievestocks.RetrieveStocksMentionsMapper.toMention;
import static com.stocksstats.stocksstats.utils.retrievestocks.RetrieveStocksMentionsMapper.toOrigin;

@Service
public class RetrieveStocksMentionsService {

    @Autowired
    private MentionRepo mentionRepo;
    @Autowired
    private OriginRepo originRepo;

    private final Reddit4J client = Initializer.client;
    private Map<Integer, String> symbols = Initializer.stockSymbols;
    private final List<StockAnalyzed> stockAnalyzedList = new ArrayList<>();

    @Value("${thread.pool.size:20}")
    private int THREAD_POOL_SIZE;

    // Se ejecuta una vez al dia a las 12:00pm
    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Madrid")
    public void analyzeSubreddit() {
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            var posts = client.getSubredditPosts("wallstreetbets", Sorting.NEW)
                    .limit(100).submit();

            for (final RedditPost post : posts) {

                executor.execute(() -> processPost(post));
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }

            save();
        } catch (IOException | InterruptedException | AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        for (final var stockAnalyzed : stockAnalyzedList) {
            var mention = mentionRepo.save(toMention(stockAnalyzed));

            List<Origin> originList = new ArrayList<>();
            for (final var origin : stockAnalyzed.getOrigin()) {
                final var originDate = origin.getDate() != null ? origin.getDate() : LocalDate.now();

                Origin mappedOrigin = toOrigin(origin.getUrl(), origin.getText(), mention, originDate);
                originList.add(mappedOrigin);
            }

            originRepo.saveAll(originList);
        }
    }

    private void processPost(RedditPost post) {
        try {
            var comments = client.getCommentsForPost("wallstreetbets", post.getId()).limit(100).submit();
            for (final RedditComment comment : comments) {

                processComment(comment);
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.getLogger(RetrieveStocksMentionsService.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void processComment(RedditComment comment) {
        String body = comment.getBody();

        for (final var entry : symbols.entrySet()) {
            if (body != null && body.contains(entry.getValue()) && !isModOrBot(comment)) {
                updateStockAnalysis(comment, body, entry.getKey(), entry.getValue());
            }
        }

    }

    private void updateStockAnalysis(RedditComment comment, String body, Integer symbolId, String symbolName) {
        synchronized (stockAnalyzedList) {
            var stockAnalyzed = locateStockAnalyzed(stockAnalyzedList, symbolId);
            var origin = new StockAnalyzed.DetectionOrigin(comment.getLinkUrl(), body, Instant.ofEpochSecond(
                    comment.getCreatedUtc())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate());

            if (stockAnalyzed == null) {
                var stock = new Stock();
                stock.setId(symbolId);
                stock.setSymbol(symbolName);

                stockAnalyzed = StockAnalyzed.builder()
                        .stock(stock)
                        .amount((short) 1)
                        .origin(new ArrayList<>(List.of(origin)))
                        .build();
                stockAnalyzedList.add(stockAnalyzed);
            } else {
                stockAnalyzed.setAmount((short) (stockAnalyzed.getAmount() + 1));
                stockAnalyzed.getOrigin().add(origin);
            }
        }
    }

    private static StockAnalyzed locateStockAnalyzed(List<StockAnalyzed> stockAnalyzedlist, Integer symbolId) {
        for (final var stockAnalyzed : stockAnalyzedlist) {
            if (stockAnalyzed.getStock().getId().equals(symbolId)) {
                return stockAnalyzed;
            }
        }
        return null;
    }

    private static boolean isModOrBot(RedditComment comment) {
        return comment.getAuthor().toLowerCase().contains("bot")  ||
                comment.getAuthor().toLowerCase().contains("mod") ||
                comment.getBody().toLowerCase().contains("bot");
    }

}
