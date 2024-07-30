"use client";

import React, { useState, useEffect, useMemo } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { DatePicker } from '@/components/DatePicker'
import { StockChart } from '@/components/StockChart'
import { StockSelector } from '@/components/StockSelector'
import { useMentions } from '@/hooks/useMentions'
import { ChartDataPoint } from '@/types'

export default function Home() {
  const [date, setDate] = useState<Date>(new Date())
  const [selectedStocks, setSelectedStocks] = useState<string[]>([])
  const { mentions, isLoading, error, fetchMentions } = useMentions()

  useEffect(() => {
    fetchMentions(date)
    setSelectedStocks([]) // Clear selected stocks when date changes
  }, [date, fetchMentions])

  const handleDateChange = (newDate: Date | undefined) => {
    if (newDate) {
      setDate(newDate)
    }
  }

  const handleStockSelect = (value: string) => {
    setSelectedStocks(prev => 
      prev.includes(value) ? prev.filter(stock => stock !== value) : [...prev, value]
    )
  }

  const chartData: ChartDataPoint[] = useMemo(() => 
    selectedStocks.map(stock => {
      const mention = mentions.find(m => m.stockSymbol === stock)
      return {
        symbol: stock,
        amount: mention ? mention.count : 0
      }
    }),
    [selectedStocks, mentions]
  )

  const total = useMemo(() => 
    chartData.reduce((acc, curr) => acc + curr.amount, 0),
    [chartData]
  )

  const handleRemoveStock = (stock: string) => {
    setSelectedStocks(prev => prev.filter(s => s !== stock))
  }

  const handleSelectTop20 = () => {
    const top20 = mentions.slice(0, 20).map(mention => mention.stockSymbol)
    setSelectedStocks(top20)
  }

  if (error) {
    return <div className="text-red-500">{error}</div>
  }

  return (
    <div className="container mx-auto p-4">
      <Card>
        <CardHeader>
          <CardTitle>Stock Mentions Chart</CardTitle>
          <CardDescription>Total Mentions: {total}</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-between mb-4">
            <DatePicker date={date} onDateChange={handleDateChange} />
            <Button onClick={handleSelectTop20} disabled={isLoading}>Select Top 20</Button>
          </div>
          {isLoading ? (
            <div>Loading...</div>
          ) : (
            <>
              <div className="relative">
                <div className={`${selectedStocks.length === 0 ? 'blur-sm' : ''}`}>
                  <StockChart data={chartData} />
                </div>
                {selectedStocks.length === 0 && (
                  <div className="absolute inset-0 flex items-center justify-center">
                    <div className="bg-white p-4 rounded shadow-lg">
                      No stocks selected. Please select a stock to view the chart.
                    </div>
                  </div>
                )}
              </div>
              <div className="mt-4">
                <StockSelector 
                  mentions={mentions} 
                  selectedStocks={selectedStocks}
                  onStockSelect={handleStockSelect} 
                />
              </div>
              <div className="mt-4 flex flex-wrap gap-2">
                {selectedStocks.map(stock => (
                  <Badge key={stock} variant="secondary" onClick={() => handleRemoveStock(stock)}>
                    {stock} <span className="ml-1 cursor-pointer">×</span>
                  </Badge>
                ))}
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  )
}