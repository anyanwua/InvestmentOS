package com.investmentos.investmentos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class StockService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate(); //Spring class that lets the backend hake http requests to other APIs

    public Stock getStock(String ticker) {

        ticker = ticker.toUpperCase();

        // -------------------------
        // 1. Get quote information
        // -------------------------

        String quoteUrl =
                "https://finnhub.io/api/v1/quote?symbol="
                + ticker
                + "&token="
                + apiKey;

        Map<String, Object> quote =
                restTemplate.getForObject(quoteUrl, Map.class); //asks for an http get request to the url, converts the json response to a java map

        //each key in the map corresponds to one of these metrics we collect & show
        double price =
                ((Number) quote.get("c")).doubleValue();

        double change =
                ((Number) quote.get("d")).doubleValue();

        double percentChange =
                ((Number) quote.get("dp")).doubleValue();

        double high =
                ((Number) quote.get("h")).doubleValue();

        double low =
                ((Number) quote.get("l")).doubleValue();

        double previousClose =
                ((Number) quote.get("pc")).doubleValue();
        
        


        // -------------------------
        // 2. Get company profile
        // -------------------------

        String profileUrl =
                "https://finnhub.io/api/v1/stock/profile2?symbol="
                + ticker
                + "&token="
                + apiKey;

        Map<String, Object> profile =
                restTemplate.getForObject(profileUrl, Map.class);

        String companyName =
                (String) profile.get("name");

        double marketCap =
                ((Number) profile.get("marketCapitalization")).doubleValue();

        String industry =
                (String) profile.get("finnhubIndustry");

        String logo =
                (String) profile.get("logo");

       
        // -------------------------
        // 3. Get financial metrics
        // -------------------------

        String metricsUrl =
        "https://finnhub.io/api/v1/stock/metric?symbol="
        + ticker
        + "&metric=all"
        + "&token="
        + apiKey;

        Map<String, Object> metricsResponse =
        restTemplate.getForObject(metricsUrl, Map.class);

        Map<String, Object> metric =
        (Map<String, Object>) metricsResponse.get("metric");

        double peRatio =
        ((Number) metric.get("peTTM")).doubleValue();

        double week52High =
        ((Number) metric.get("52WeekHigh")).doubleValue();

        double week52Low =
        ((Number) metric.get("52WeekLow")).doubleValue();

        double beta =
        ((Number) metric.get("beta")).doubleValue();
        double eps =
                ((Number) metric.get("epsTTM")).doubleValue();


        // -------------------------
        // 4. Create Stock object
        // -------------------------

        return new Stock(
            ticker,
            companyName,
            price,
            marketCap,
            change,
            percentChange,
            high,
            low,
            previousClose,
            industry,
            logo,
            peRatio,
            week52High,
            week52Low,
            beta,
            eps

    );
    }
}