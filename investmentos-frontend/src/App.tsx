import { useState } from "react";
import "./App.css";

type Stock = {
  ticker: string;
  companyName: string;
  price: number;
  marketCap: number;
  change: number;
  percentChange: number;
  high: number;
  low: number;
  previousClose: number;
  industry: string;
  logo: string;
  peRatio: number;
  week52High: number;
  week52Low: number;
  beta: number;
  eps: number;
};
type InvestmentBrief = {
  overview: string;
  bullCase: string[];
  bearCase: string[];
  risks: string[];
  valuationCommentary: string;
};
type NewsArticle = {
  id: number;
  headline: string;
  summary: string;
  source: string;
  url: string;
  image: string;
  datetime: number;
};

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
    maximumFractionDigits: 2,
  }).format(value);
}

function formatMarketCap(value: number) {
  // Finnhub reports market cap in millions
  const dollars = value * 1_000_000;

  if (dollars >= 1_000_000_000_000) {
    return `$${(dollars / 1_000_000_000_000).toFixed(2)}T`;
  }

  if (dollars >= 1_000_000_000) {
    return `$${(dollars / 1_000_000_000).toFixed(2)}B`;
  }

  if (dollars >= 1_000_000) {
    return `$${(dollars / 1_000_000).toFixed(2)}M`;
  }

  return formatCurrency(dollars);
}

function App() {
  const [ticker, setTicker] = useState("");
  const [stock, setStock] = useState<Stock | null>(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const [targetPE, setTargetPE] = useState(25);

  const [brief, setBrief] = useState<InvestmentBrief | null>(null);
  const [briefLoading, setBriefLoading] = useState(false);
  const [briefError, setBriefError] = useState("");

  const [news, setNews] = useState<NewsArticle[]>([]);
const [newsLoading, setNewsLoading] = useState(false);
const [newsError, setNewsError] = useState("");

  const fairValue = stock
    ? stock.eps * targetPE
    : 0;

  const upside = stock
    ? ((fairValue - stock.price) / stock.price) * 100
    : 0;

    
    async function searchStock() {
      if (!ticker.trim()) {
        setError("Please enter a ticker symbol.");
        setStock(null);
        return;
      }
    
      setLoading(true);
      setError("");
    
      setBrief(null);
      setBriefError("");
      setNews([]);
    
      try {
        const response = await fetch(
          `http://localhost:8080/api/stocks/${ticker.trim().toUpperCase()}`
        );
    
        if (!response.ok) {
          throw new Error("Stock not found.");
        }
    
        const data = await response.json();
    
        if (!data.companyName || data.price === 0) {
          throw new Error("Stock not found.");
        }
    
        setStock(data);
    
        // Fire these off after stock data succeeds
        loadNews(data.ticker);
        generateBrief(data, targetPE);
    
      } catch (error) {
        console.error(error);
    
        setStock(null);
        setBrief(null);
        setNews([]);
    
        setError(
          "Stock not found. Please check the ticker symbol."
        );
      } finally {
        setLoading(false);
      }
    }
    async function generateBrief(stockData: Stock, pe: number) {
      setBriefLoading(true);
      setBriefError("");
    
      try {
        const response = await fetch(
          `http://localhost:8080/api/stocks/${stockData.ticker}/analysis?targetPE=${pe}`
        );
    
        if (!response.ok) {
          throw new Error("Failed to generate AI brief.");
        }
    
        const data = await response.json();
    
        setBrief(data);
      } catch (error) {
        console.error(error);
        setBriefError("Could not generate AI brief.");
      } finally {
        setBriefLoading(false);
      }
    }
  
    async function loadNews(ticker: string) {
      setNewsLoading(true);
      setNewsError("");
    
      try {
        const response = await fetch(
          `http://localhost:8080/api/stocks/${ticker}/news`
        );
    
        if (!response.ok) {
          throw new Error("Failed to load news.");
        }
    
        const data = await response.json();
    
        setNews(data);
      } catch (error) {
        console.error(error);
        setNewsError("Could not load recent news.");
      } finally {
        setNewsLoading(false);
      }
    }

  return (
    <div className="app">
      <h1>InvestmentOS</h1>

      <div className="search">
        <input
          type="text"
          placeholder="Enter ticker..."
          value={ticker}
          onChange={(e) => setTicker(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter") {
              searchStock();
            }
          }}
        />

      <button onClick={searchStock} disabled={loading}>
        {loading ? "Loading..." : "Analyze"}
      </button>
      </div>
      {error && <p className="error-message">{error}</p>}
      {stock && (
        <div className="stock-card">
          <div className="stock-header">

<div className="company-info">
  {stock.logo && (
    <img
      src={stock.logo}
      alt={stock.companyName}
      className="logo"
    />
  )}

  <div>
    <h2>{stock.companyName}</h2>

    <p className="stock-subtitle">
      {stock.ticker} · {stock.industry}
    </p>
  </div>
</div>

<div className="price-info">

  <h2>{formatCurrency(stock.price)}</h2>

  <p
    className={
      stock.change >= 0
        ? "positive-change"
        : "negative-change"
    }
  >
    {stock.change >= 0 ? "+" : ""}
    {formatCurrency(stock.change)}
    {" "}
    ({stock.percentChange >= 0 ? "+" : ""}
    {stock.percentChange.toFixed(2)}%)
  </p>

</div>

</div>


          <div className="metrics">
            <div>
              <strong>Market Cap</strong>
              <p>{formatMarketCap(stock.marketCap)}</p>
            </div>

            <div>
              <strong>P/E Ratio</strong>
              <p>{stock.peRatio}</p>
            </div>

            <div>
              <strong>52W High</strong>
              <p>{formatCurrency(stock.week52High)}</p>
            </div>

            <div>
              <strong>52W Low</strong>
              <p>{formatCurrency(stock.week52Low)}</p>
            </div>

            <div>
              <strong>Beta</strong>
              <p>{stock.beta}</p>
            </div>

            <div>
            <strong>Previous Close</strong>
            <p>{formatCurrency(stock.previousClose)}</p>
            </div>
          </div>

          <div className="valuation-section">
  <h2>Valuation</h2>

  <div className="valuation-grid">

    <div className="valuation-item">
      <span>Current Price</span>
      <strong>{formatCurrency(stock.price)}</strong>
    </div>

    <div className="valuation-item">
      <span>EPS (TTM)</span>
      <strong>{formatCurrency(stock.eps)}</strong>
    </div>

    <div className="valuation-item">
  <span>Target P/E</span>

  <strong>{targetPE.toFixed(1)}x</strong>

  <input
    type="range"
    min="10"
    max="50"
    step="1"
    value={targetPE}
    onChange={(e) => setTargetPE(Number(e.target.value))}
  />
  <div className="range-labels">
  <span>10x</span>
  <span>50x</span>
</div>
</div>


    <div className="valuation-item">
      <span>Estimated Fair Value</span>
      <strong>{formatCurrency(fairValue)}</strong>
    </div>

  </div>

  <div className="valuation-summary">
    <span>Potential Upside / Downside</span>

    <strong
      className={
        upside >= 0
          ? "positive-change"
          : "negative-change"
      }
    >
      {upside >= 0 ? "+" : ""}
      {upside.toFixed(2)}%
    </strong>
  </div>

  <p className="valuation-note">
    Estimated fair value is based on trailing EPS and a simplified
    target P/E multiple. This is not investment advice.
  </p>

</div>
<div className="ai-section">
<div className="ai-header">
  <h2>AI Investment Brief</h2>

  {briefLoading && (
    <span>Generating analysis...</span>
  )}
</div>

  {briefError && (
    <p className="error-message">{briefError}</p>
  )}

  {brief && (
    <div className="brief-content">

      <div className="brief-block">
        <h3>Overview</h3>
        <p>{brief.overview}</p>
      </div>

      <div className="brief-grid">

        <div className="brief-block">
          <h3>Bull Case</h3>
          <ul>
            {brief.bullCase.map((point, index) => (
              <li key={index}>{point}</li>
            ))}
          </ul>
        </div>

        <div className="brief-block">
          <h3>Bear Case</h3>
          <ul>
            {brief.bearCase.map((point, index) => (
              <li key={index}>{point}</li>
            ))}
          </ul>
        </div>

      </div>

      <div className="brief-block">
        <h3>Key Risks</h3>
        <ul>
          {brief.risks.map((risk, index) => (
            <li key={index}>{risk}</li>
          ))}
        </ul>
      </div>

      <div className="brief-block">
        <h3>Valuation Commentary</h3>
        <p>{brief.valuationCommentary}</p>
      </div>

    </div>
  )}
</div>
<div className="news-section">

<div className="news-header">
  <h2>Latest News</h2>
</div>

  {newsError && (
    <p className="error-message">{newsError}</p>
  )}

{newsLoading && (
  <p className="news-empty">
    Loading latest news...
  </p>
)}

{!newsLoading && news.length === 0 && !newsError && (
  <p className="news-empty">
    No recent news found.
  </p>
)}

  <div className="news-list">

    {news.map((article) => (

      <a
        key={article.id}
        href={article.url}
        target="_blank"
        rel="noopener noreferrer"
        className="news-card"
      >

        {article.image && (
          <img
            src={article.image}
            alt=""
            className="news-image"
          />
        )}

        <div className="news-content">

          <div className="news-source">
            {article.source}
          </div>

          <h3>{article.headline}</h3>

          {article.summary && (
            <p>{article.summary}</p>
          )}

          <span className="news-time">
            {new Date(
              article.datetime * 1000
            ).toLocaleString()}
          </span>

        </div>

      </a>

    ))}

  </div>

</div>
        </div>
      )}
    </div>
  );
}

export default App;