package com.stocksstats.stocksstats;

import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditComment;
import masecla.reddit4j.objects.Sorting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class StocksStatsApplication {

    public static void main(String[] args) throws AuthenticationException, IOException, InterruptedException {
        SpringApplication.run(StocksStatsApplication.class, args);

        Properties props = new Properties();
        try (InputStream is = new FileInputStream("src/main/resources/reddit-credentials.properties")) {
            props.load(is);
        }

        Reddit4J client = Reddit4J.rateLimited()
                .setClientId(props.getProperty("api-key"))
                .setClientSecret(props.getProperty("secret-key"))
                .setUserAgent(new UserAgentBuilder().appname("stocks-stats").author("putotonto").version("1.0"));

        List<String> symbols = Files.readAllLines(Paths.get("src/main/resources/stocks.txt"));

        client.userlessConnect();
        var stockAnalyzedList = new ArrayList<StockAnalyzed>();
        String subreddit = "wallstreetbets";
        ExecutorService executor = Executors.newFixedThreadPool(20);
        var res = client.getSubredditPosts(subreddit, Sorting.NEW).limit(100).submit();
        res.forEach(post -> {
            executor.execute(() -> {
                try {
                    List<RedditComment> comments = client.getCommentsForPost(subreddit, post.getId()).limit(100).submit();
                    for (RedditComment comment : comments) {
                        String body = comment.getBody();

                        symbols.forEach(symbol -> {
                            if (body != null && body.contains(symbol) && !isModOrBot(comment)) {
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
                        });

                        try {
                            String response = body + "\n----------------\n";
                            Files.write(Paths.get("src/main/resources/response.txt"), response.getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
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
