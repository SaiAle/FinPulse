import axios from 'axios';
import type { PriceQuote, PortfolioSummary, MarketInsight } from '@/types/market';

const api = axios.create({ baseURL: '/api/v1' });

export const marketApi = {
  getQuote:    (symbol: string)          => api.get<PriceQuote>(`/market/quote/${symbol}`).then(r => r.data),
  getQuotes:   (symbols: string[])       => api.get<PriceQuote[]>(`/market/quotes?symbols=${symbols.join(',')}`).then(r => r.data),
  getInsight:  (symbol: string)          => api.get<MarketInsight>(`/market/insight/${symbol}`).then(r => r.data),
};

export const portfolioApi = {
  getSummary:  (portfolioId: number)     => api.get<PortfolioSummary>(`/portfolio/${portfolioId}/summary`).then(r => r.data),
};

export const watchlistApi = {
  getWatchlist:(userId: string)          => api.get<PriceQuote[]>(`/watchlist/${userId}`).then(r => r.data),
  addSymbol:   (userId: string, symbol: string) => api.post(`/watchlist/${userId}`, { symbol }).then(r => r.data),
  removeSymbol:(userId: string, symbol: string) => api.delete(`/watchlist/${userId}/${symbol}`).then(r => r.data),
};
