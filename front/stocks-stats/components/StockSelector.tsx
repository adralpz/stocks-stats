import React from 'react'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Mention } from '../types'

interface StockSelectorProps {
  mentions: Mention[];
  selectedStocks: string[];
  onStockSelect: (value: string) => void;
}

export function StockSelector({ mentions, selectedStocks, onStockSelect }: StockSelectorProps) {
  return (
    <Select onValueChange={onStockSelect}>
      <SelectTrigger className="w-full">
        <SelectValue placeholder="Select a stock" />
      </SelectTrigger>
      <SelectContent>
        {mentions.map((mention) => (
          <SelectItem key={mention.stockId} value={mention.stockSymbol}>
            {mention.stockSymbol} ({mention.count})
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  )
}