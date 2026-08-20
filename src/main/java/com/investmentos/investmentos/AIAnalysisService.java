package com.investmentos.investmentos;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;

import org.springframework.stereotype.Service;

@Service

//utilizes only the metrics provided by the backend to create an objective AI investment brief.
public class AIAnalysisService {

    private final OpenAIClient client;

    public AIAnalysisService() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    public InvestmentBrief analyzeStock(
            Stock stock,
            double targetPE,
            double fairValue) {

        String prompt = """
                You are an equity research assistant.

                Analyze the company using ONLY the financial information
                supplied below.

                Do not invent financial figures.
                Do not claim certainty about future stock performance.
                Keep the analysis concise and useful.

                Company: %s
                Ticker: %s
                Industry: %s

                Current Price: %.2f
                Market Cap: %.2f
                P/E Ratio: %.2f
                EPS TTM: %.2f
                Beta: %.2f
                52 Week High: %.2f
                52 Week Low: %.2f

                User Target P/E: %.2f
                Estimated Fair Value: %.2f

                Produce:
                - a short business/valuation overview
                - 3 bull case points
                - 3 bear case points
                - 3 key risks
                - brief valuation commentary
                """
                .formatted(
                        stock.getCompanyName(),
                        stock.getTicker(),
                        stock.getIndustry(),
                        stock.getPrice(),
                        stock.getMarketCap(),
                        stock.getPeRatio(),
                        stock.getEps(),
                        stock.getBeta(),
                        stock.getWeek52High(),
                        stock.getWeek52Low(),
                        targetPE,
                        fairValue
                );

        StructuredResponseCreateParams<InvestmentBrief> params =
                ResponseCreateParams.builder()
                        .input(prompt)
                        .model(ChatModel.GPT_5_2)
                        .text(InvestmentBrief.class)
                        .build();

        StructuredResponse<InvestmentBrief> response =
                client.responses().create(params);

                return response.output().stream()
                .flatMap(output -> output.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("AI analysis returned no result")
                );
    }
}