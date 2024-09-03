"use client";

import React, {useState, useEffect, useMemo} from 'react'
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card"
import {Badge} from "@/components/ui/badge"
import {Button} from "@/components/ui/button"
import {DatePicker} from '@/components/DatePicker'
import {StockChart} from '@/components/StockChart'
import {StockSelector} from '@/components/StockSelector'
import {useMentions} from '@/hooks/useMentions'
import {ChartDataPoint} from '@/types'
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion"

export default function Home() {
  const [date, setDate] = useState<Date>(new Date(new Date().setDate(new Date().getDate() - 1)))
  const [selectedStocks, setSelectedStocks] = useState<string[]>([])
  const {mentions, isLoading, error, fetchMentions} = useMentions()

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
      <div className="flex flex-col min-h-screen">
        <main className="flex-grow container mx-auto p-4 pb-16">
          <Card>
            <CardHeader>
              <CardTitle>Stock Mentions</CardTitle>
              <CardDescription>Total Mentions: {total}</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <DatePicker date={date} onDateChange={handleDateChange}/>
                <Button onClick={handleSelectTop20} disabled={isLoading}>20 Top Mentions</Button>
              </div>
              <div className="space-y-2"> {/* Contenedor con espaciado vertical reducido */}
                <div className="relative min-h-[300px]"> {/* Altura mínima reducida */}
                  {isLoading ? (
                      <div className="absolute inset-0 flex items-center justify-center">
                        <div className="text-lg font-semibold">Loading...</div>
                      </div>
                  ) : (
                      <>
                        <div className={`${selectedStocks.length === 0 ? 'blur-sm' : ''}`}>
                          <StockChart data={chartData}/>
                        </div>
                        {selectedStocks.length === 0 && (
                            <div className="absolute inset-0 flex items-center justify-center">
                              <div className="bg-white p-4 rounded shadow-lg">
                                Please select a stock to view the information
                              </div>
                            </div>
                        )}
                      </>
                  )}
                </div>
                <StockSelector
                    mentions={mentions}
                    selectedStocks={selectedStocks}
                    onStockSelect={handleStockSelect}
                />
              </div>
              <div className="flex flex-wrap gap-2">
                {selectedStocks.map(stock => (
                    <Badge key={stock} variant="secondary" onClick={() => handleRemoveStock(stock)}>
                      {stock} <span className="ml-1 cursor-pointer">×</span>
                    </Badge>
                ))}
              </div>
            </CardContent>
          </Card>

          <Accordion type="single" collapsible className="w-full mt-4">
            <AccordionItem value="item-1">
              <AccordionTrigger>What is this application for?</AccordionTrigger>
              <AccordionContent>
                This application analyzes a stock-related subreddit daily. It identifies and counts mentions of each
                stock
                symbol. This helps users see which stocks are being talked about the most on that day.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-2">
              <AccordionTrigger>Where does the information come from?</AccordionTrigger>
              <AccordionContent>
                Currently, the information comes from a subreddit r/wallstreetbets. In the future, I plan to expand our
                data sources to provide more comprehensive and detailed information.
              </AccordionContent>
            </AccordionItem>
            <AccordionItem value="item-3">
              <AccordionTrigger>Are you planning to expand this project?</AccordionTrigger>
              <AccordionContent>
              Yes, I plan to add reputation system for each ticker symbol. This will allow users to see the stocks that
                are trending and how is the people reacting to them.
              </AccordionContent>
            </AccordionItem>
          </Accordion>

        </main>

        <footer className="w-full mt-auto border-t border-gray-200 bg-white">
          <div className="container mx-auto px-4 py-4">
            <div className="flex justify-center items-center">
              <a
                  href="https://github.com/adralpz/stocks-stats/"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-sm text-gray-600 hover:text-gray-900 transition-colors"
              >
                Give me a star!
              </a>
            </div>
          </div>
        </footer>
      </div>
  )
}