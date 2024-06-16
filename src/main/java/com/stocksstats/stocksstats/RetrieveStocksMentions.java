package com.stocksstats.stocksstats;

import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class RetrieveStocksMentions {

    private final Reddit4J client;
    private final List<String> symbols;
    private final List<StockAnalyzed> stockAnalyzedList = new ArrayList<>();
    private final ExecutorService executor;

    //TODO parametrizar
    private final int THREAD_POOL_SIZE = 20;

    @Autowired
    public RetrieveStocksMentions(List<String> symbols) {
    //    public RetrieveStocksMentions(Reddit4J client, List<String> symbols) {
    //        this.client = client;
        //TODO Que el autowired funcione con el cliente
        client = null;
        this.symbols = symbols;
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void analyzeSubreddit(String subreddit) {
        try {
            var posts = client.getSubredditPosts(subreddit, Sorting.NEW)
                    .limit(100).submit();

            for (RedditPost post : posts) {
                executor.execute(() -> processPost(subreddit, post));
            }

        } catch (IOException | InterruptedException | AuthenticationException e) {
            throw new RuntimeException(e);
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
            //TODO Reemplazar por logger
            System.err.println(e.getMessage());
        }
    }


    private void processComment(RedditComment comment) {
        String body = comment.getBody();

        for (String symbol : symbols) {
            if (body != null && body.contains(symbol) && !isModOrBot(comment)) {
                updateStockAnalysis(comment, body, symbol);
            }
        }

        logInResponseFile(body);
    }

    private void updateStockAnalysis(RedditComment comment, String body, String symbol) {
        synchronized (stockAnalyzedList) {
            StockAnalyzed stockAnalyzed = findStockAnalyzed(stockAnalyzedList, symbol);
            StockAnalyzed.DetectionOrigin origin =
                    new StockAnalyzed.DetectionOrigin(comment.getLinkUrl(), body);

            if (stockAnalyzed == null) {
                stockAnalyzed = StockAnalyzed.builder()
                        .stock(symbol)
                        .amount(1)
                        .origin(new ArrayList<>(List.of(origin)))
                        .build();
                stockAnalyzedList.add(stockAnalyzed);
            } else {
                stockAnalyzed.setAmount(stockAnalyzed.getAmount() + 1);
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
        return comment.getDistinguished().toLowerCase().equals("moderator") ||
                comment.getAuthor().toLowerCase().contains("bot");
    }

}
