package com.stocksstats.stocksstats.service.retrievestocks;

import com.stocksstats.stocksstats.config.Initializer;
import com.stocksstats.stocksstats.dto.StockAnalyzed;
import com.stocksstats.stocksstats.entity.Origin;
import com.stocksstats.stocksstats.entity.Stock;
import com.stocksstats.stocksstats.repository.MentionRepo;
import com.stocksstats.stocksstats.repository.OriginRepo;
import jakarta.annotation.PostConstruct;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.stocksstats.stocksstats.utils.retrievestocks.RetrieveStockMentionsMapper.toMention;
import static com.stocksstats.stocksstats.utils.retrievestocks.RetrieveStockMentionsMapper.toOrigin;


@Service
public class RetrieveStocksMentionsService {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveStocksMentionsService.class);

    @Autowired
    private MentionRepo mentionRepo;

    @Autowired
    private OriginRepo originRepo;

    @Autowired
    private Initializer initializer;

    private Reddit4J client;
    private Map<Integer, String> symbols;
    private final List<StockAnalyzed> stockAnalyzedList = new ArrayList<>();

    @Value("${thread.pool.size:20}")
    private int THREAD_POOL_SIZE;

    @PostConstruct
    public void init() {
        this.client = initializer.getClient();
        this.symbols = initializer.getStockSymbols();
        if (this.client == null || this.symbols == null) {
            logger.error("Failed to initialize client or symbols from Initializer");
            throw new IllegalStateException("Failed to initialize from Initializer");
        }
    }

    // Se ejecuta una vez al dia a las 12:00pm
    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Madrid")
    public void analyzeSubreddit() {
        logger.info("Starting subreddit analysis");
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            var posts = client.getSubredditPosts("wallstreetbets", Sorting.NEW)
                    .limit(100).submit();

            for (final RedditPost post : posts) {
                executor.execute(() -> processPost(post));
            }

            executor.shutdown();
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                logger.warn("Executor did not terminate in the specified time.");
                executor.shutdownNow();
            }

            save();
        } catch (IOException | InterruptedException | AuthenticationException e) {
            logger.error("Error during subreddit analysis", e);
            throw new RuntimeException("Failed to analyze subreddit", e);
        }
        logger.info("Subreddit analysis completed");
    }

    private void save() {
        logger.info("Saving analyzed stocks");
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
        logger.info("Saved {} analyzed stocks", stockAnalyzedList.size());
    }

    private void processPost(RedditPost post) {
        try {
            logger.debug("Processing post: {}", post.getId());
            var comments = client.getCommentsForPost("wallstreetbets", post.getId()).limit(100).submit();
            for (final RedditComment comment : comments) {
                processComment(comment);
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            logger.error("Error processing post: " + post.getId(), e);
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