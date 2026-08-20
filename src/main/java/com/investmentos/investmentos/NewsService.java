package com.investmentos.investmentos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<NewsArticle> getCompanyNews(String ticker) {

        ticker = ticker.toUpperCase();

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        String url =
                "https://finnhub.io/api/v1/company-news"
                + "?symbol=" + ticker
                + "&from=" + sevenDaysAgo
                + "&to=" + today
                + "&token=" + apiKey;

        List<Map<String, Object>> response =
                restTemplate.getForObject(url, List.class);

        List<NewsArticle> articles = new ArrayList<>();

        if (response == null) {
            return articles;
        }

        for (Map<String, Object> item : response) {

            NewsArticle article = new NewsArticle(
                    getLong(item, "id"),
                    getString(item, "headline"),
                    getString(item, "summary"),
                    getString(item, "source"),
                    getString(item, "url"),
                    getString(item, "image"),
                    getLong(item, "datetime")
            );

            articles.add(article);

            if (articles.size() == 6) {
                break;
            }
        }

        return articles;
    }

    private String getString(Map<String, Object> item, String key) {
        Object value = item.get(key);
        return value == null ? "" : value.toString();
    }

    private long getLong(Map<String, Object> item, String key) {
        Object value = item.get(key);

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        return 0;
    }
}