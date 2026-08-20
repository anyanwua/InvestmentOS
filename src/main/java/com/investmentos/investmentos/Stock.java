package com.investmentos.investmentos;

//This class holds all the information regarding an indicated stock collected from the FinhubAPI
public class Stock {

    private String ticker;
    private String companyName;
    private double price;
    private double marketCap;

    private double eps;

    private double change;
    private double percentChange;
    private double high;
    private double low;
    private double previousClose;

    private double peRatio;
    private double week52High;
    private double week52Low;
    private double beta;      

    private String industry;
    private String logo;

    public Stock(
            String ticker,
            String companyName,
            double price,
            double marketCap,
            double change,
            double percentChange,
            double high,
            double low,
            double previousClose,
            String industry,
            String logo,
            double peRatio,
            double week52High,
            double week52Low,
            double beta,
            double eps) {

        this.ticker = ticker;
        this.companyName = companyName;
        this.price = price;
        this.marketCap = marketCap;
        this.change = change;
        this.percentChange = percentChange;
        this.high = high;
        this.low = low;
        this.previousClose = previousClose;
        this.industry = industry;
        this.logo = logo;
        this.peRatio = peRatio;
        this.week52High = week52High;
        this.week52Low = week52Low;
        this.beta = beta;
        this.eps = eps;
    }

    public String getTicker() {
        return ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public double getChange() {
        return change;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public String getIndustry() {
        return industry;
    }

    public String getLogo() {
        return logo;
    }
    public double getPeRatio() {
        return peRatio;
    }
    
    public double getWeek52High() {
        return week52High;
    }
    
    public double getWeek52Low() {
        return week52Low;
    }
    
    public double getBeta() {
        return beta;
    }
    public double getEps() {
        return eps;
    }
}