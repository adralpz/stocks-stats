package com.stocksstats.stocksstats.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.stocksstats.stocksstats.dto.StockAnalyzed;
import com.stocksstats.stocksstats.entity.Origin;
import com.stocksstats.stocksstats.repository.OriginRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.stocksstats.stocksstats.config.Initializer;
import com.stocksstats.stocksstats.entity.Mention;
import com.stocksstats.stocksstats.repository.MentionRepo;

import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;

@Service
public class RetrieveStocksMentionsService {

    @Autowired
    private MentionRepo mentionRepo;
    @Autowired
    private OriginRepo originRepo;

    private final Reddit4J client =Initializer.client;
    private final List<String> symbols = Initializer.stockSymbols;
    private final List<StockAnalyzed> stockAnalyzedList = new ArrayList<>();

    @Value("${thread.pool.size:20}")
    private int THREAD_POOL_SIZE;

    // Se ejecuta una vez al dia a las 12:00pm
    @Scheduled(cron = "0 0 12 * * *", zone = "Europe/Madrid")
    public void analyzeSubreddit() {
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE)) {
            var posts = client.getSubredditPosts("wallstreetbets", Sorting.NEW)
                    .limit(100).submit();

            for (RedditPost post : posts) {
                executor.execute(() -> processPost("wallstreetbets", post));
            }

            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }

            save();
        } catch (IOException | InterruptedException | AuthenticationException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        for (StockAnalyzed stockAnalyzed : stockAnalyzedList) {
            Mention mention = mentionRepo.save(toMention(stockAnalyzed));

            originRepo.saveAll(stockAnalyzed.getOrigin().stream()
                    .map(origin -> toOrigin(origin.getUrl(), origin.getText(), mention))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), originRepo::saveAll)));
        }
    }

    private void processPost(String subreddit, RedditPost post) {
        try {
            List<RedditComment> comments = client.getCommentsForPost(subreddit, post.getId()).limit(100).submit();
            for (RedditComment comment : comments) {
                processComment(comment);
            }

            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO Reemplazar por logger
            System.err.println(e.getMessage());
        }
    }

    private void processComment(RedditComment comment) {
        String body = comment.getBody();

        for (String symbol : symbols) {
            if (body != null && body.contains(symbol) && isModOrBot(comment)) {
                updateStockAnalysis(comment, body, symbol);
            }
        }

        logInResponseFile(body);
    }

    private void updateStockAnalysis(RedditComment comment, String body, String symbol) {
        synchronized (stockAnalyzedList) {
            StockAnalyzed stockAnalyzed = findStockAnalyzed(stockAnalyzedList, symbol);
            StockAnalyzed.DetectionOrigin origin = new StockAnalyzed.DetectionOrigin(comment.getLinkUrl(), body);

            if (stockAnalyzed == null) {
                stockAnalyzed = StockAnalyzed.builder()
                        .stock(symbol)
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

    private void logInResponseFile(String body) {
        try {
            String response = String.format("%s %s", body, "\n---------------------------------\n");
            Files.write(Paths.get("src/main/resources/response.txt"), response.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static StockAnalyzed findStockAnalyzed(List<StockAnalyzed> list, String symbol) {
        for (StockAnalyzed stockAnalyzed : list) {
            if (stockAnalyzed.getStock().equals(symbol)) {
                return stockAnalyzed;
            }
        }
        return null;
    }

    public static boolean isModOrBot(RedditComment comment) {
        return comment.getAuthor().toLowerCase().contains("bot")  ||
                comment.getAuthor().toLowerCase().contains("mod");
    }

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
