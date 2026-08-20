package com.investmentos.investmentos;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:5173")
@RestController

//This class serves as the entry point for HTTP requests
public class StockController {


    private final StockService stockService; 
    private final AIAnalysisService aiAnalysisService; 
    private final NewsService newsService; 

    public StockController(
        StockService stockService,
        AIAnalysisService aiAnalysisService,
        NewsService newsService) {

    this.stockService = stockService;
    this.aiAnalysisService = aiAnalysisService;
    this.newsService = newsService;
}
    //returns a stock opject when the http get request comes to the url
    @GetMapping("/api/stocks/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        return stockService.getStock(ticker);
    }
    //this method runs when the analysis is requested (was formally button activated)
     @GetMapping("/api/stocks/{ticker}/analysis")
    public InvestmentBrief getAnalysis(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "25") double targetPE) {
    
        Stock stock = stockService.getStock(ticker);
    
        double fairValue =
                stock.getEps() * targetPE;
        
        return aiAnalysisService.analyzeStock(
                stock,
                targetPE,
                fairValue
        );
    }
    @GetMapping("/api/stocks/{ticker}/news")
public List<NewsArticle> getNews(
        @PathVariable String ticker) {

    return newsService.getCompanyNews(ticker);
}



}