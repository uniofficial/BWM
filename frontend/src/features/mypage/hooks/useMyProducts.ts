import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { getMyProducts } from '../../../api/myProductApi'
import type { MyProductItem } from '../../../types/myProduct'
import { getMyPageErrorMessage } from '../utils/myPageFormat'

export function useMyProducts() {
  const [products, setProducts] = useState<MyProductItem[] | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const requestIdRef = useRef(0)

  useEffect(() => {
    const controller = new AbortController()
    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId
    setIsLoading(true)
    setError(null)
    void getMyProducts(controller.signal)
      .then((nextProducts) => {
        if (requestIdRef.current === requestId && !controller.signal.aborted) setProducts(nextProducts)
      })
      .catch((reason: unknown) => {
        if (requestIdRef.current === requestId && !axios.isCancel(reason)) setError(getMyPageErrorMessage(reason))
      })
      .finally(() => {
        if (requestIdRef.current === requestId && !controller.signal.aborted) setIsLoading(false)
      })
    return () => controller.abort()
  }, [retryKey])

  return {
    products,
    isLoading,
    error,
    refresh: () => {
      requestIdRef.current += 1
      setRetryKey((value) => value + 1)
    },
  }
}
