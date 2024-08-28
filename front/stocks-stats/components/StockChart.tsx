import { ResponsiveContainer, BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip } from "recharts"
import { ChartDataPoint } from '../types'

interface StockChartProps {
  data: ChartDataPoint[];
  className?: string;
}

export function StockChart({ data, className = '' }: StockChartProps) {
  return (
    <div className={`h-[300px] w-full ${className}`}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="symbol" />
          <YAxis />
          <Tooltip />
          <Bar dataKey="amount" fill="#2a9d90" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  )
}