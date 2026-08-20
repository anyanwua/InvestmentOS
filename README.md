# InvestmentOS

InvestmentOS is a full-stack equity research dashboard that combines real-time market data, fundamental valuation, company news, and AI-generated investment analysis in a single interface.

The application uses a React + TypeScript frontend with a Java Spring Boot backend. Market data and company news are retrieved from Finnhub, while the OpenAI API generates structured investment briefs grounded in financial data supplied by the backend.

## Features

### Stock Research
Search for publicly traded companies by ticker symbol and view key market and fundamental data, including:

- Current stock price
- Daily price change
- Market capitalization
- P/E ratio
- EPS (TTM)
- Beta
- 52-week high and low
- Industry information

### Interactive Valuation

InvestmentOS includes a simplified P/E-based valuation model.

Users can adjust the target P/E multiple and immediately see the resulting estimated fair value and potential upside or downside.

The model uses:

```text
Estimated Fair Value = EPS (TTM) × Target P/E
```

Potential upside/downside is calculated as:

```text
Upside / Downside (%) =
((Estimated Fair Value - Current Price) / Current Price) × 100
```

The valuation model is intended as a research tool and not as investment advice.

### AI Investment Brief

InvestmentOS uses the OpenAI API to generate a structured equity research brief based on financial information collected by the backend.

The brief includes:

- Company and valuation overview
- Bull case
- Bear case
- Key risks
- Valuation commentary

The application supplies the model with structured financial data rather than relying on the model to retrieve or invent current market figures.

### Company News

Recent company-specific news is retrieved automatically for each searched ticker.

Each article includes available information such as:

- Headline
- Publisher
- Summary
- Publication time
- Article image
- Link to the original source

## Tech Stack

### Frontend

- React
- TypeScript
- CSS
- Fetch API

### Backend

- Java
- Spring Boot
- Maven
- REST APIs
- Spring `RestTemplate`

### External APIs

- Finnhub API — market fundamentals and company news
- OpenAI API — structured AI investment briefs

## Architecture

```text
                         InvestmentOS

┌──────────────────────────────────────────────────────┐
│                  React + TypeScript                  │
│                                                      │
│  Search │ Stock Data │ Valuation │ AI Brief │ News  │
└──────────────────────────┬───────────────────────────┘
                           │
                        REST API
                           │
                           ▼
┌──────────────────────────────────────────────────────┐
│                    Spring Boot                       │
│                                                      │
│                  StockController                     │
│                         │                            │
│          ┌──────────────┼──────────────┐             │
│          ▼              ▼              ▼             │
│    StockService     NewsService   AIAnalysisService  │
│          │              │              │             │
└──────────┼──────────────┼──────────────┼─────────────┘
           │              │              │
           ▼              ▼              ▼
        Finnhub        Finnhub         OpenAI
       Market Data      News        Responses API
```

## Request Flow

When a user searches for a ticker such as `AAPL`:

```text
User searches AAPL
        │
        ▼
React sends request
        │
        ▼
Spring Boot
        │
        ├──── Fetch stock fundamentals from Finnhub
        │
        ├──── Fetch recent company news from Finnhub
        │
        └──── Generate structured investment analysis
        │                 │
        │                 ▼
        │               OpenAI
        │
        ▼
React receives the results
        │
        ▼
Dashboard updates
```

## AI Analysis Pipeline

The AI layer is intentionally separated from market-data retrieval.

```text
Finnhub
   │
   ▼
Financial Data
   │
   ▼
StockService
   │
   ▼
Stock Object
   │
   ├──── EPS
   ├──── P/E
   ├──── Price
   ├──── Beta
   ├──── Market Cap
   └──── 52-Week Range
   │
   ▼
Java Valuation Model
   │
   ▼
AIAnalysisService
   │
   ▼
OpenAI
   │
   ▼
Structured InvestmentBrief
   │
   ▼
React Dashboard
```

The Java backend performs the explicit valuation calculations. The AI layer receives those results along with the underlying financial metrics and produces qualitative analysis.

## API Endpoints

### Stock Data

```http
GET /api/stocks/{ticker}
```

Example:

```http
GET /api/stocks/AAPL
```

Returns financial and market information for the requested company.

### Company News

```http
GET /api/stocks/{ticker}/news
```

Example:

```http
GET /api/stocks/AAPL/news
```

Returns recent company-specific news.

### AI Investment Analysis

```http
GET /api/stocks/{ticker}/analysis?targetPE={multiple}
```

Example:

```http
GET /api/stocks/AAPL/analysis?targetPE=25
```

Returns a structured AI-generated investment brief using the company's financial data and selected valuation assumption.

## Running Locally

### Prerequisites

Install:

- Java
- Maven (or use the included Maven wrapper)
- Node.js
- npm

You will also need API credentials for Finnhub and OpenAI.

### Environment Variables

InvestmentOS keeps API credentials outside of the source code.

Set the following environment variables:

```bash
export FINNHUB_API_KEY="your_finnhub_api_key"
export OPENAI_API_KEY="your_openai_api_key"
```

Do not commit real API keys to GitHub.

### Start the Backend

From the project root:

```bash
./mvnw spring-boot:run
```

The Spring Boot API runs locally on:

```text
http://localhost:8080
```

### Start the Frontend

Open another terminal:

```bash
cd investmentos-frontend
npm install
npm run dev
```

The development server will display the local frontend address in the terminal.

## Project Structure

```text
InvestmentOS/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ...
│   │   │       ├── StockController.java
│   │   │       ├── StockService.java
│   │   │       ├── NewsService.java
│   │   │       ├── AIAnalysisService.java
│   │   │       ├── Stock.java
│   │   │       ├── NewsArticle.java
│   │   │       └── InvestmentBrief.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│
├── investmentos-frontend/
│   ├── src/
│   │   ├── App.tsx
│   │   └── App.css
│   └── package.json
│
├── .gitignore
├── pom.xml
├── mvnw
└── README.md
```

## Design Decisions

### Backend API Proxy

External API requests are made by the Spring Boot backend instead of directly from React. This prevents private API credentials from being exposed in client-side code and keeps external-service logic centralized in the backend.

### Service Layer

External integrations are separated into dedicated services:

- `StockService` handles market and fundamental data.
- `NewsService` handles company news.
- `AIAnalysisService` handles AI-generated research.

The controller remains responsible primarily for routing HTTP requests to the appropriate services.

### Structured AI Output

The AI response is mapped into an `InvestmentBrief` Java object containing structured fields for the overview, bull case, bear case, risks, and valuation commentary.

This allows the frontend to render each part of the analysis independently rather than processing an unstructured block of generated text.

## Future Improvements

Potential future additions include:

- User accounts
- Persistent watchlists
- Historical price charts
- Portfolio tracking
- Additional valuation models such as DCF
- Earnings and financial-statement analysis
- Company comparison tools
- Database persistence
- Automated testing
- Production deployment

## Disclaimer

InvestmentOS is an educational software project. Information and AI-generated analysis presented by the application should not be considered financial or investment advice.
