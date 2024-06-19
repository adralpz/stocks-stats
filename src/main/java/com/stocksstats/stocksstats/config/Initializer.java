package com.stocksstats.stocksstats.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.stocksstats.stocksstats.repository.StockRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;

@Data
@Configuration
public class Initializer {

    @Autowired
    private StockRepo stockRepo;

    public static Reddit4J client;
    public static List<String> stockSymbols;
    public static List<String> subreddits;

    @Value("classpath:reddit-credentials.properties")
    private Resource credentialsResource;


    @PostConstruct
    public void start() {
        try {
            initClient();
            Initializer.stockSymbols = stocksSymbols();
            // TODO Elaborar una lista de subreddits y recuparla desde aqui

        } catch (Exception ex) {
            // TODO Reemplazar con logger
            System.err.println("Error de mierda: " + ex.getMessage());
        }
    }

    private void initClient() throws AuthenticationException, IOException, InterruptedException {
        Properties props = retrieveCredentials();

        Reddit4J client = Reddit4J.rateLimited()
                .setClientId(props.getProperty("api-key"))
                .setClientSecret(props.getProperty("secret-key"))
                .setUserAgent(new UserAgentBuilder().appname("stocks-stats").author("putotonto").version("1.0"));
        client.userlessConnect();

        Initializer.client = client;
    }

    private Properties retrieveCredentials() {
        try (InputStream is = credentialsResource.getInputStream()) {
            Properties props = new Properties();
            props.load(is);
            return props;
        } catch (Exception ex) {
            // TODO Reemplazar por logger
            System.err.println("Error al encontrar el archivo de credenciales");
            return null;
        }
    }

    private List<String> stocksSymbols() {
        var stocksSymbols = new ArrayList<String>();
        try {
            stockRepo.findAll().forEach(stock -> stocksSymbols.add(stock.getSymbol()));
            return stocksSymbols;
        } catch (Exception ignored) {
            // TODO Reemplazar por logger
            System.err.println("Error al leer el archivo de simbolos de stocks");
        }
        return List.of("NVDA", "MSFT", "AMZN");
    }

}
