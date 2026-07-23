import axios from 'axios'
import { useCallback, useEffect, useRef, useState } from 'react'
import { getProductDetail } from '../../../api/productApi'
import type { ProductDetail } from '../../../types/product'
import {
  parseProductDetailError,
  shouldRetryProductPolling,
  type ProductDetailLoadError,
} from '../utils/productDetailErrors'

const POLLING_INTERVAL_MS = 5_000
const noRefresh = async () => false
const noControl = () => undefined

export function useProductPolling(productId: number | null, retryKey: number) {
  const [product, setProduct] = useState<ProductDetail | null>(null)
  const [isInitialLoading, setIsInitialLoading] = useState(productId !== null)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [initialError, setInitialError] = useState<ProductDetailLoadError | null>(null)
  const [refreshError, setRefreshError] = useState<string | null>(null)
  const refreshRunnerRef = useRef<() => Promise<boolean>>(noRefresh)
  const pauseRunnerRef = useRef<() => void>(noControl)
  const resumeRunnerRef = useRef<() => void>(noControl)

  const refreshProduct = useCallback(() => refreshRunnerRef.current(), [])
  const pausePolling = useCallback(() => pauseRunnerRef.current(), [])
  const resumePolling = useCallback(() => resumeRunnerRef.current(), [])

  useEffect(() => {
    if (productId === null) {
      setProduct(null)
      setIsInitialLoading(false)
      setInitialError(null)
      return undefined
    }

    let active = true
    let paused = false
    let hasLoaded = false
    let timeoutId: number | undefined
    let controller: AbortController | undefined
    let latestRequestId = 0

    setProduct(null)
    setIsInitialLoading(true)
    setIsRefreshing(false)
    setInitialError(null)
    setRefreshError(null)

    const clearScheduledPoll = () => {
      if (timeoutId !== undefined) {
        window.clearTimeout(timeoutId)
        timeoutId = undefined
      }
    }

    const scheduleNext = () => {
      clearScheduledPoll()
      if (!active || paused || document.visibilityState === 'hidden') return
      timeoutId = window.setTimeout(() => void loadProduct(), POLLING_INTERVAL_MS)
    }

    const loadProduct = async (): Promise<boolean> => {
      if (!active || paused || document.visibilityState === 'hidden') return false

      clearScheduledPoll()
      controller?.abort()
      const requestId = ++latestRequestId
      const requestController = new AbortController()
      controller = requestController
      if (hasLoaded) setIsRefreshing(true)
      let shouldContinue = true

      try {
        const nextProduct = await getProductDetail(productId, requestController.signal)
        if (!active || requestId !== latestRequestId) return false

        setProduct(nextProduct)
        hasLoaded = true
        setInitialError(null)
        setRefreshError(null)
        shouldContinue = nextProduct.status === 'OPEN'
        return true
      } catch (error) {
        if (!active || requestId !== latestRequestId || axios.isCancel(error)) return false

        const parsedError = parseProductDetailError(error)
        if (!hasLoaded) {
          setInitialError(parsedError)
          shouldContinue = false
        } else {
          setRefreshError(parsedError.message)
          shouldContinue = shouldRetryProductPolling(parsedError)
        }
        return false
      } finally {
        if (active && requestId === latestRequestId) {
          setIsInitialLoading(false)
          setIsRefreshing(false)
          if (shouldContinue) scheduleNext()
        }
      }
    }

    refreshRunnerRef.current = loadProduct
    pauseRunnerRef.current = () => {
      paused = true
      clearScheduledPoll()
      latestRequestId += 1
      controller?.abort()
      setIsRefreshing(false)
    }
    resumeRunnerRef.current = () => {
      if (!active || !paused) return
      paused = false
      scheduleNext()
    }

    const handleVisibilityChange = () => {
      if (document.visibilityState === 'hidden') {
        clearScheduledPoll()
        latestRequestId += 1
        controller?.abort()
        setIsRefreshing(false)
        return
      }

      if (active && !paused) void loadProduct()
    }

    document.addEventListener('visibilitychange', handleVisibilityChange)
    void loadProduct()
    return () => {
      active = false
      document.removeEventListener('visibilitychange', handleVisibilityChange)
      clearScheduledPoll()
      latestRequestId += 1
      controller?.abort()
      refreshRunnerRef.current = noRefresh
      pauseRunnerRef.current = noControl
      resumeRunnerRef.current = noControl
    }
  }, [productId, retryKey])

  return {
    product,
    isInitialLoading,
    isRefreshing,
    initialError,
    refreshError,
    refreshProduct,
    pausePolling,
    resumePolling,
  }
}
