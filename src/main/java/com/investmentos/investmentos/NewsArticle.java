package com.investmentos.investmentos;

public class NewsArticle {

    private long id;
    private String headline;
    private String summary;
    private String source;
    private String url;
    private String image;
    private long datetime;

    public NewsArticle(
            long id,
            String headline,
            String summary,
            String source,
            String url,
            String image,
            long datetime) {

        this.id = id;
        this.headline = headline;
        this.summary = summary;
        this.source = source;
        this.url = url;
        this.image = image;
        this.datetime = datetime;
    }

    public long getId() {
        return id;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSummary() {
        return summary;
    }

    public String getSource() {
        return source;
    }

    public String getUrl() {
        return url;
    }

    public String getImage() {
        return image;
    }

    public long getDatetime() {
        return datetime;
    }
}