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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
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

        // read file 'symbols.txt' and get list of symbols
        List<String> symbols = Files.readAllLines(Paths.get("src/main/resources/stocks.txt"));

        client.userlessConnect();

        // ConcurrentHashMap to handle concurrent modifications
        var stockMentions = new ConcurrentHashMap<String, Integer>();

        String subreddit = "wallstreetbets";

//        symbols.forEach(symbol -> {
//            executor.execute(() -> {
//                res.stream()
//                        .forEach(post -> {
//                            try {
//                                List<RedditComment> comments = client.getCommentsForPost(subreddit, post.getId()).submit();
//                                for (RedditComment comment : comments) {
//                                    if (comment.getBody().contains(symbol)) {
//                                        synchronized (stockMentions) {
//                                            stockMentions.put(symbol, stockMentions.getOrDefault(symbol, 0) + 1);
//                                        }
//                                    }
//                                }
//                            } catch (Exception ignored) {
//                            }
//                        });
//            });
//        });
//


        ExecutorService executor = Executors.newFixedThreadPool(20);

        var res = client.getSubredditPosts(subreddit, Sorting.NEW).submit();
        res.stream().forEach(post -> {
            executor.execute(() -> {
                try {
                    List<RedditComment> comments = client.getCommentsForPost(subreddit, post.getId()).submit();
                    for (RedditComment comment : comments) {
                        String body = comment.getBody();

                        symbols.forEach(symbol -> {
                            if (body != null && body.contains(symbol)) {
                                stockMentions.merge(symbol, 1, Integer::sum);
                                System.out.println(comment.getAuthor() + "\n-----------------------------\n" + body);
                                System.out.println("-----------------------------\n");
                            }
                        });
                    }
                    // sleep for 1 second to avoid rate limit
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

        stockMentions.forEach((k, v) -> System.out.println(k + " " + v));
    }
}
