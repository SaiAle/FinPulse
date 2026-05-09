import React from 'react';
import { Sparkles, TrendingUp, TrendingDown, Minus } from 'lucide-react';
import type { MarketInsight } from '@/types/market';

interface Props { insight: MarketInsight | null; loading: boolean; symbol: string; }

export const InsightCard: React.FC<Props> = ({ insight, loading, symbol }) => {
  if (loading) return (
    <div className="rounded-xl border border-fin-border bg-fin-card p-5">
      <div className="flex items-center gap-2 text-gray-400 text-sm">
        <Sparkles className="h-4 w-4 animate-pulse text-purple-400" />
        Generating AI insight for {symbol}...
      </div>
    </div>
  );
  if (!insight) return null;

  const sentimentConfig = {
    BULLISH:  { color: 'text-fin-green', bg: 'bg-green-950', icon: TrendingUp },
    BEARISH:  { color: 'text-fin-red',   bg: 'bg-red-950',   icon: TrendingDown },
    NEUTRAL:  { color: 'text-gray-400',  bg: 'bg-gray-800',  icon: Minus },
  };
  const signalConfig = {
    BUY:  'bg-green-900 text-fin-green border-green-800',
    SELL: 'bg-red-900 text-fin-red border-red-800',
    HOLD: 'bg-gray-800 text-gray-300 border-gray-700',
  };

  const cfg = sentimentConfig[insight.sentiment] || sentimentConfig.NEUTRAL;
  const Icon = cfg.icon;

  return (
    <div className="rounded-xl border border-fin-border bg-fin-card p-5 space-y-3">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="h-4 w-4 text-purple-400" />
          <span className="text-sm font-semibold text-gray-200">AI Insight · {insight.symbol}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className={`flex items-center gap-1 rounded-full px-2 py-1 text-xs font-bold ${cfg.bg} ${cfg.color}`}>
            <Icon className="h-3 w-3" />
            {insight.sentiment}
          </span>
          <span className={`rounded border px-2 py-1 text-xs font-bold ${signalConfig[insight.signal]}`}>
            {insight.signal}
          </span>
        </div>
      </div>
      <p className="text-sm text-gray-200">{insight.summary}</p>
      <p className="text-xs text-gray-500">{insight.rationale}</p>
    </div>
  );
};
