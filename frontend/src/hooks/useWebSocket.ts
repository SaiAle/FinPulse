import { useEffect, useRef, useState, useCallback } from 'react';
import type { PriceQuote } from '@/types/market';

export function usePriceStream(symbols: string[]) {
  const ws = useRef<WebSocket | null>(null);
  const [quotes, setQuotes] = useState<Record<string, PriceQuote>>({});
  const [connected, setConnected] = useState(false);

  const connect = useCallback(() => {
    if (!symbols.length) return;
    const url = `ws://${window.location.host}/ws/prices?symbols=${symbols.join(',')}`;
    ws.current = new WebSocket(url);

    ws.current.onopen  = () => setConnected(true);
    ws.current.onclose = () => { setConnected(false); setTimeout(connect, 3000); };
    ws.current.onmessage = (ev) => {
      try {
        const q: PriceQuote = JSON.parse(ev.data);
        setQuotes(prev => ({ ...prev, [q.symbol]: q }));
      } catch {}
    };
  }, [symbols.join(',')]);

  useEffect(() => {
    connect();
    return () => ws.current?.close();
  }, [connect]);

  return { quotes, connected };
}
