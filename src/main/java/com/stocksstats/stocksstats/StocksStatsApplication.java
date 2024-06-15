package com.stocksstats.stocksstats;

import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import masecla.reddit4j.objects.RedditPost;
import masecla.reddit4j.objects.RedditUser;
import masecla.reddit4j.objects.Sorting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

        client.userlessConnect();

        client.getSubredditPosts("wallstreetbets", Sorting.TOP).submit().forEach(System.out::println);

    }

}
