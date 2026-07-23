import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { getMyChargeRequests } from '../../../api/walletApi'
import type { ChargeRequestHistoryItem } from '../../../types/wallet'
import { getMyPageErrorMessage } from '../utils/myPageFormat'

export function useChargeHistory() {
  const [rows, setRows] = useState<ChargeRequestHistoryItem[] | null>(null)
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
    void getMyChargeRequests(controller.signal)
      .then((nextRows) => {
        if (requestIdRef.current === requestId && !controller.signal.aborted) setRows(nextRows)
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
    rows,
    isLoading,
    error,
    refresh: () => {
      requestIdRef.current += 1
      setRetryKey((value) => value + 1)
    },
  }
}
