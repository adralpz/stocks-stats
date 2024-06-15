package com.stocksstats.stocksstats;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import masecla.reddit4j.client.Reddit4J;
import masecla.reddit4j.client.UserAgentBuilder;
import masecla.reddit4j.exceptions.AuthenticationException;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Data
@Component
public final class Initializer {

    public static Reddit4J client;
    public static List<String> stockSymbols;
    public static List<String> subreddits;

    @Value("classpath:reddit-credentials.properties")
    private Resource credentialsResource;

    @Value("classpath:stocks.txt")
    private Resource stocksResource;


    @PostConstruct
    public void start() {
        try {
            initClient();
            Initializer.stockSymbols = stocksSymbols();

            //TODO Elaborar una lista de subreddits y recuparla desde aqui

        } catch (Exception ex) {
            //TODO Reemplazar con logger
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
            //TODO Reemplazar por logger
            System.err.println("Error al encontrar el archivo de credenciales");
            return null;
        }
    }

    private List<String> stocksSymbols() {
        var stocksSymbols = new ArrayList<String>();
        try {
           stocksSymbols.addAll(Files.readAllLines(Paths.get(stocksResource.getURI())));
           return stocksSymbols;
        } catch (Exception ignored) {
            //TODO Reemplazar por logger
            System.err.println("Error al leer el archivo de simbolos de stocks");
        }
        return List.of("NVDA","MSFT", "AMZN");
    }

}
