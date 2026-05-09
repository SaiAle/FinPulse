import React from 'react';
import { TrendingUp, TrendingDown } from 'lucide-react';
import type { PortfolioSummary } from '@/types/market';

interface Props { portfolio: PortfolioSummary | null; loading: boolean; }

export const PortfolioPanel: React.FC<Props> = ({ portfolio, loading }) => {
  if (loading) return (
    <div className="flex h-40 items-center justify-center text-gray-500 text-sm">
      Loading portfolio...
    </div>
  );
  if (!portfolio) return null;

  const isUp = Number(portfolio.totalPnlPercent) >= 0;

  return (
    <div className="space-y-4">
      <div className="rounded-xl border border-fin-border bg-fin-card p-5">
        <p className="text-xs text-gray-400">Total Portfolio Value</p>
        <p className="mt-1 text-3xl font-bold">${Number(portfolio.totalValue).toLocaleString('en-US', { minimumFractionDigits: 2 })}</p>
        <div className={`mt-2 flex items-center gap-1 text-sm font-medium ${isUp ? 'text-fin-green' : 'text-fin-red'}`}>
          {isUp ? <TrendingUp className="h-4 w-4" /> : <TrendingDown className="h-4 w-4" />}
          {isUp ? '+' : ''}${Number(portfolio.totalPnl).toFixed(2)} ({isUp ? '+' : ''}{portfolio.totalPnlPercent}%)
        </div>
      </div>

      <div className="space-y-2">
        {portfolio.holdings.map(h => {
          const hIsUp = Number(h.pnlPercent) >= 0;
          const barWidth = Math.min(Number(h.allocationPercent), 100);
          return (
            <div key={h.symbol} className="rounded-lg border border-fin-border bg-fin-card px-4 py-3">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-semibold">{h.symbol}</p>
                  <p className="text-xs text-gray-500">{h.quantity} shares @ ${Number(h.avgCost).toFixed(2)}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm font-semibold">${Number(h.marketValue).toLocaleString()}</p>
                  <p className={`text-xs font-medium ${hIsUp ? 'text-fin-green' : 'text-fin-red'}`}>
                    {hIsUp ? '+' : ''}{h.pnlPercent}%
                  </p>
                </div>
              </div>
              <div className="mt-2 h-1 w-full rounded-full bg-gray-800">
                <div className="h-1 rounded-full bg-fin-blue" style={{ width: `${barWidth}%` }} />
              </div>
              <p className="mt-1 text-right text-xs text-gray-600">{h.allocationPercent}%</p>
            </div>
          );
        })}
      </div>
    </div>
  );
};
