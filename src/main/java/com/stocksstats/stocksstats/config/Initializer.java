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
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@Configuration
public class Initializer {
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private StockRepo stockRepo;

    public static Reddit4J client;
    public static Map<Integer, String> stockSymbols;

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
            logger.error("Error durante la inicialización", ex);
            throw new RuntimeException("Fallo en la inicialización", ex);
        }
    }

    private void initClient() throws AuthenticationException, IOException, InterruptedException {
        var props = retrieveCredentials();

        final var client = Reddit4J.rateLimited()
                .setClientId(props.getProperty("api-key"))
                .setClientSecret(props.getProperty("secret-key"))
                .setUserAgent(new UserAgentBuilder().appname("stocks-stats")
                        .author("putotonto").version("1.0"));

        clientBuilder.userlessConnect();
        logger.info("Cliente Reddit4J inicializado correctamente");
    }

    private Properties retrieveCredentials() {
        try (final var is = credentialsResource.getInputStream()) {
            var props = new Properties();

            props.load(is);
            return props;
        } catch (Exception ex) {
            logger.error("Error al encontrar el archivo de credenciales", ex);
            return null;
        }
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