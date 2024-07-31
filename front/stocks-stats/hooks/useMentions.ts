"use client";

import { useState, useEffect, useCallback } from 'react'
import { Mention } from '@/types'

export function useMentions() {
  const [mentions, setMentions] = useState<Mention[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const API_URL = process.env.NEXT_PUBLIC_API_URL || '';

  const fetchMentions = useCallback(async (date: Date) => {
    setIsLoading(true)
    setError(null)
    try {
      const response = await fetch(`${API_URL}/mentions-count?date=${date.toISOString().split('T')[0]}`)
      if (!response.ok) throw new Error('Failed to fetch mentions')
      const data = await response.json()
      setMentions(data || [])
    } catch (error) {
      console.error('Error fetching mentions:', error)
      setError('Failed to fetch mentions. Please try again.')
      setMentions([])
    } finally {
      setIsLoading(false)
    }
  }, [])

  return { mentions, isLoading, error, fetchMentions }
}