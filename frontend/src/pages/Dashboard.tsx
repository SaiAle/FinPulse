import React, { useEffect, useState, useCallback } from 'react';
import { Activity, Wifi, WifiOff, RefreshCw, Plus, X } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';
import { marketApi, portfolioApi, watchlistApi } from '@/services/api';
import { PriceCard } from '@/components/PriceCard';
import { PortfolioPanel } from '@/components/PortfolioPanel';
import { InsightCard } from '@/components/InsightCard';
import { usePriceStream } from '@/hooks/useWebSocket';
import type { PriceQuote, PortfolioSummary, MarketInsight } from '@/types/market';

const DEMO_USER = 'demo-user';
const PORTFOLIO_ID = 1;
const DEFAULT_SYMBOLS = ['AAPL', 'MSFT', 'GOOGL', 'NVDA', 'TSLA', 'AMZN'];

export const Dashboard: React.FC = () => {
  const [watchlist, setWatchlist]       = useState<PriceQuote[]>([]);
  const [portfolio, setPortfolio]       = useState<PortfolioSummary | null>(null);
  const [insight, setInsight]           = useState<MarketInsight | null>(null);
  const [selectedSymbol, setSelectedSymbol] = useState<string>('AAPL');
  const [priceHistory, setPriceHistory] = useState<Record<string, number[]>>({});
  const [loadingPortfolio, setLoadingPortfolio] = useState(true);
  const [loadingInsight, setLoadingInsight]     = useState(false);
  const [addSymbol, setAddSymbol]       = useState('');
  const [symbols, setSymbols]           = useState<string>(DEFAULT_SYMBOLS);

  const { quotes, connected } = usePriceStream(symbols);

  // Accumulate price history (last 30 ticks)
  useEffect(() => {
    Object.entries(quotes).forEach(([sym, q]) => {
      setPriceHistory(prev => ({
        ...prev,
        [sym]: [...(prev[sym] || []).slice(-29), Number(q.price)],
      }));
    });
  }, [quotes]);

  // Merge live quotes into watchlist
  const displayList = watchlist.map(w => quotes[w.symbol] || w);

  const loadPortfolio = useCallback(async () => {
    try {
      setLoadingPortfolio(true);
      const p = await portfolioApi.getSummary(PORTFOLIO_ID);
      setPortfolio(p);
    } finally {
      setLoadingPortfolio(false);
    }
  }, []);

  const loadWatchlist = useCallback(async () => {
    const wl = await watchlistApi.getWatchlist(DEMO_USER);
    setWatchlist(wl);
    setSymbols(wl.map(w => w.symbol));
  }, []);

  const loadInsight = useCallback(async (sym: string) => {
    setLoadingInsight(true);
    setInsight(null);
    try {
      const ins = await marketApi.getInsight(sym);
      setInsight(ins);
    } finally {
      setLoadingInsight(false);
    }
  }, []);

  useEffect(() => { loadPortfolio(); loadWatchlist(); }, []);

  const handleSelect = (sym: string) => {
    setSelectedSymbol(sym);
    loadInsight(sym);
  };

  const handleAddSymbol = async () => {
    if (!addSymbol.trim()) return;
    const sym = addSymbol.trim().toUpperCase();
    await watchlistApi.addSymbol(DEMO_USER, sym);
    setAddSymbol('');
    loadWatchlist();
  };

  const handleRemove = async (sym: string) => {
    await watchlistApi.removeSymbol(DEMO_USER, sym);
    loadWatchlist();
  };

  const chartData = (priceHistory[selectedSymbol] || []).map((price, i) => ({ i, price }));

  return (
    <div className="min-h-screen bg-fin-bg text-gray-100">
      <header className="sticky top-0 z-10 border-b border-fin-border bg-fin-bg/80 px-6 py-3 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-fin-blue">
              <Activity className="h-5 w-5 text-white" />
            </div>
            <div>
              <h1 className="text-base font-bold">FinPulse</h1>
              <p className="text-xs text-gray-500">Real-Time Financial Analytics</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            {connected ? <span className="flex items-center gap-1 text-xs text-fin-green"><Wifi className="h-3 w-3" /> Live</span> : <span className="flex items-center gap-1 text-xs text-gray-500"><WifiOff className="h-3 w-3" /> Offline</span>}
            <button onClick={loadPortfolio} className="rounded-lg border border-fin-border p-2 text-gray-400 hover:text-white"><RefreshCw className="h-4 w-4" /></button>
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-6 py-6">
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-sm font-semibold text-gray-300">Watchlist</h2>
              <div className="flex gap-2"><input value={addSymbol} onChange={e => setAddSymbol(e.target.value.toUpperCase())} onKeyDown={e => e.key === 'Enter' && handleAddSymbol()} placeholder="Add symbol…" className="w-28 rounded-lg border border-fin-border bg-fin-card px-2 py-1 text-xs outline-none focus:border-fin-blue" /><button onClick={handleAddSymbol} className="rounded-lg bg-fin-blue px-2 py-1 text-xs font-medium text-white hover:opacity-90"><Plus className="h-3 w-3" /></button></div>
            </div>
            {displayList.map(q => (<div key={q.symbol} className="relative group"><PriceCard quote={q} onClick={() => handleSelect(q.symbol)} selected={q.symbol === selectedSymbol} /><button onClick={() => handleRemove(q.symbol)} className="absolute right-2 top-2 hidden rounded p-1 text-gray-600 hover:text-red-400 group-hover:block"><X className="h-3 w-3" /></button></div>))}
          </div>
          <div className="space-y-4 lg:col-span-2">
            <div className="rounded-xl border border-fin-border bg-fin-card p-5">
              <div className="mb-4 flex items-center justify-between"><div><h2 className="text-lg font-bold">{selectedSymbol}</h2>{selectedSymbol && quotes[selectedSymbol] && <p className="text-2xl font-bold">${Number(quotes[selectedSymbol].price).toFixed(2)}<span className={`ml-2 text-sm ${Number(quotes[selectedSymbol].changePercent) >= 0 ? 'text-fin-green' : 'text-fin-red'}`}>{Number(quotes[selectedSymbol].changePercent) >= 0 ? '+' : ''}{Number(quotes[selectedSymbol].changePercent).toFixed(2)}%</span></p>}</div><span className="rounded-full border border-fin-border px-3 py-1 text-xs text-gray-400">Live Stream</span></div>
              <ResponsiveContainer width="100%" height={220}><AreaChart data={chartData}><defs><linearGradient id="priceGrad" x1="0" y1="0" x2="0" y2="1"><stop offset="5%" stopColor="#008FFB" stopOpacity={0.3} /><stop offset="95%" stopColor="#008FFB" stopOpacity={0} /></linearGradient></defs><XAxis dataKey="i" hide /><YAxis domain={["auto","auto"]} tick={{fontSize:11,fill:"#6B7280"}} width={60} tickFormatter={v => `$${v.toFixed(0)}`} /><Tooltip contentStyle={{background:"#111827",border:"1px solid #1F2937",fontSize:12}} formatter={(v: number) => [`$${v.toFixed(2)}`,"Price"]} labelFormatter={() => ""} /><Area type="monotone" dataKey="price" stroke="#008FFB" strokeWidth={2} fill="url(#priceGrad)" dot={false} /></AreaChart></ResponsiveContainer>
            </div>
            <InsightCard insight={insight} loading={loadingInsight} symbol={selectedSymbol} />
            <div><h2 className="mb-3 text-sm font-semibold text-gray-300">Portfolio</h2><PortfolioPanel portfolio={portfolio} loading={loadingPortfolio} /></div>
          </div>
        </div>
      </main>
    </div>
  );
};
