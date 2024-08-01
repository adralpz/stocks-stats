package com.stocksstats.stocksstats.config;

import com.stocksstats.stocksstats.repository.StockRepo;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@Configuration
public class Initializer implements WebMvcConfigurer {


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
            // TODO Reemplazar con logger
            System.err.println("Error de mierda: " + ex.getMessage());
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://api.adrianlopez.tech/")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    private void initClient() throws AuthenticationException, IOException, InterruptedException {
        var props = retrieveCredentials();

        final var client = Reddit4J.rateLimited()
                .setClientId(props.getProperty("api-key"))
                .setClientSecret(props.getProperty("secret-key"))
                .setUserAgent(new UserAgentBuilder().appname("stocks-stats")
                        .author("putotonto").version("1.0"));

        client.userlessConnect();

        Initializer.client = client;
    }

    private Properties retrieveCredentials() {
        try (final var is = credentialsResource.getInputStream()) {
            var props = new Properties();

            props.load(is);
            return props;
        } catch (Exception ex) {
            // TODO Reemplazar por logger
            System.err.println("Error al encontrar el archivo de credenciales");
            return null;
        }
    }

    private LinkedHashMap<Integer, String> stocksSymbols() {
        var stocksSymbols = new LinkedHashMap<Integer, String>();
        try {
            stockRepo.findAll().forEach(stock ->
                    stocksSymbols.put(stock.getId(), stock.getSymbol()));

            return stocksSymbols;
        } catch (Exception ignored) {
            // TODO Reemplazar por logger
            System.err.println("Error al leer el archivo de simbolos de stocks");
            return null;
        }
    }

}
