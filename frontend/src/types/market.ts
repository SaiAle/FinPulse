export interface PriceQuote {
  symbol: string;
  price: number;
  change: number;
  changePercent: string;
  volume: number;
  high: number;
  low: number;
  timestamp: string;
}

export interface HoldingSummary {
  symbol: string;
  quantity: number;
  avgCost: number;
  currentPrice: number;
  marketValue: number;
  pnl: number;
  pnlPercent: string;
  allocationPercent: string;
}

export interface PortfolioSummary {
  userId: string;
  totalValue: number;
  totalCost: number;
  totalPnl: number;
  totalPnlPercent: string;
  holdings: HoldingSummary[];
}

export interface MarketInsight {
  symbol: string;
  sentiment: 'BULLISH' | 'BEARISH' | 'NEUTRAL';
  signal: 'BUY' | 'SELL' | 'HOLD';
  summary: string;
  rationale: string;
  generatedAt: string;
}
