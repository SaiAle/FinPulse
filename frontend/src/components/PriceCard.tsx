import React, { useEffect, useRef, useState } from 'react';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import type { PriceQuote } from '@/types/market';

interface Props { quote: PriceQuote; onClick?: () => void; selected?: boolean; }

export const PriceCard: React.FC<Props> = ({ quote, onClick, selected }) => {
  const [flash, setFlash] = useState('');
  const prevPrice = useRef(quote.price);

  useEffect(() => {
    if (quote.price !== prevPrice.current) {
      setFlash(quote.price > prevPrice.current ? 'flash-green' : 'flash-red');
      prevPrice.current = quote.price;
      setTimeout(() => setFlash(''), 600);
    }
  }, [quote.price]);

  const isUp = Number(quote.changePercent) >= 0;
  const color = isUp ? 'text-fin-green' : 'text-fin-red';

  return (
    <div
      onClick={onClick}
      className={`${flash} cursor-pointer rounded-xl border p-4 transition-all
        ${selected
          ? 'border-fin-blue bg-blue-950/30'
          : 'border-fin-border bg-fin-card hover:border-gray-600'}`}
    >
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs font-medium text-gray-400">{quote.symbol}</p>
          <p className="mt-1 text-xl font-bold">${Number(quote.price).toFixed(2)}</p>
        </div>
        <div className={`flex items-center gap-1 rounded-full px-2 py-1 text-xs font-semibold
          ${isUp ? 'bg-green-950 text-fin-green' : 'bg-red-950 text-fin-red'}`}>
          {isUp ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
          {Math.abs(Number(quote.changePercent)).toFixed(2)}%
        </div>
      </div>
      <div className="mt-2 flex items-center justify-between text-xs text-gray-500">
        <span className={color}>{isUp ? '+' : ''}{Number(quote.change).toFixed(2)}</span>
        <span>Vol {(quote.volume / 1_000_000).toFixed(1)}M</span>
      </div>
    </div>
  );
};
