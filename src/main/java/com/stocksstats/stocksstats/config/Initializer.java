package com.stocksstats.stocksstats.config;

import com.stocksstats.stocksstats.repository.StockRepo;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Component
@Data
public class Initializer {
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private StockRepo stockRepo;

    private Reddit4J client;
    private Map<Integer, String> stockSymbols;
    private List<String> subreddits;

    @Value("${app.reddit.api-key}")
    private String redditApiKey;
    @Value("${app.reddit.secret-key}")
    private String redditSecretKey;

    @PostConstruct
    public void init() {
        try {
            initClient();
            this.stockSymbols = stocksSymbols();
            // TODO: Elaborar una lista de subreddits y recuperarla desde aquí
        } catch (Exception ex) {
            logger.error("Error durante la inicialización", ex);
            throw new RuntimeException("Fallo en la inicialización", ex);
        }
    }

    private void initClient() throws AuthenticationException, IOException, InterruptedException {

        this.client = Reddit4J.rateLimited()
                .setClientId(redditApiKey)
                .setClientSecret(redditSecretKey)
                .setUserAgent(new UserAgentBuilder().appname("stocks-stats")
                        .author("putotonto").version("1.0"));

        client.userlessConnect();
        logger.info("Cliente Reddit4J inicializado correctamente");
    }

    private LinkedHashMap<Integer, String> stocksSymbols() {
        var stocksSymbols = new LinkedHashMap<Integer, String>();
        try {
            stockRepo.findAll().forEach(stock ->
                    stocksSymbols.put(stock.getId(), stock.getSymbol()));
            logger.info("Símbolos de stocks cargados correctamente: {} símbolos", stocksSymbols.size());
            return stocksSymbols;
        } catch (Exception ex) {
            logger.error("Error al leer los símbolos de stocks", ex);
            throw new RuntimeException("No se pudieron cargar los símbolos de stocks", ex);
        }
    }

}