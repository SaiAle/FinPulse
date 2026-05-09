import React, { useMemo } from 'react';
import { LineChart, Line, ResponsiveContainer, Tooltip } from 'recharts';
import type { PriceQuote } from '@/types/market';

interface Props { history: number[]; color: string; }

export const MiniChart: React.FC<Props> = ({ history, color }) => {
  const data = history.map((price, i) => ({ i, price }));
  return (
    <ResponsiveContainer width="100%" height={48}>
      <LineChart data={data}>
        <Line type="monotone" dataKey="price" stroke={color} strokeWidth={1.5} dot={false} />
        <Tooltip
          contentStyle={{ background: '#111827', border: '1px solid #1F2937', fontSize: 11 }}
          formatter={(v: number) => [`$${v.toFixed(2)}`, '']}
          labelFormatter={() => ''}
        />
      </LineChart>
    </ResponsiveContainer>
  );
};
